package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net"
	"net/http"
	"net/rpc"
	"os"
	"strings"

	"github.com/DistributedClocks/GoVector/govec"
	"github.com/DistributedClocks/GoVector/govec/vrpc"
)

// Config 配置
type Config struct {
	ModuleIDs map[string][]string
}

// ModuleID 该模块ID
var ModuleID string
var config Config
var logger *govec.GoLog
var loggerOptions govec.GoLogOptions

//managerRPCServer RPC句柄
type managerRPCServer int

// 初始化
func initialize() {
	ModuleID = os.Args[1]
	ReadMinerConfig("config.json", &config)
	fmt.Printf("%+v\n", config)
	logger = govec.InitGoVector(ModuleID, ModuleID+"-logfile", govec.GetDefaultConfig())
	loggerOptions = govec.GetDefaultLogOptions()
	go spawnRPCServer()
}

// spawnRPCServer RPC 端口监听, 监听IP为config.MinerIPPort
func spawnRPCServer() {
	mrpc := new(managerRPCServer)
	server := rpc.NewServer()
	server.Register(mrpc)
	// println(config.ModuleIDs[ModuleID])
	l, e := net.Listen("tcp", config.ModuleIDs[ModuleID][0])
	if e != nil {
		log.Fatal("listen error:", e)
	}

	options := govec.GetDefaultLogOptions()
	vrpc.ServeRPCConn(server, l, logger, options)
}

func main() {
	if len(os.Args) != 2 {
		log.Fatal("go run *.go -id")
	}
	initialize()

	http.HandleFunc("/puzzle/distribute", distribute)
	err := http.ListenAndServe(config.ModuleIDs[ModuleID][1], nil) //设置监听的端口
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}

func distribute(w http.ResponseWriter, r *http.Request) {
	r.ParseForm() //解析url传递的参数，对于POST则解析响应包的主体（request body）
	//注意:如果没有调用ParseForm方法，下面无法获取表单的数据

	for k, v := range r.Form {
		fmt.Println("key:", k)
		fmt.Println("val:", strings.Join(v, ""))
	}
	fmt.Fprintf(w, "Hello astaxie!") //这个写入到w的是输出到客户端的
}

/*
Name: readFileByte
@ para: filePath string
@ Return: string
Func: read and then return the byte of Content from file in corresponding path
*/
func ReadFileByte(filePath string) []byte {
	data, err := ioutil.ReadFile(filePath)
	if err != nil {
		log.Fatal(err)
	}
	return data
}

// ReadMinerConfig 读取配置文件
func ReadMinerConfig(configFile string, config *Config) {
	// Open our jsonFile
	jsonFile, err := os.Open(configFile)
	// if we os.Open returns an error then handle it
	if err != nil {
		fmt.Println(err)
	}
	json.Unmarshal([]byte(ReadFileByte(configFile)), config)
	// fmt.Println(config) // print the json setting
	defer jsonFile.Close()
}
