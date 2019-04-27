from py2neo import *
import pymongo
import time
import sys

def printBar(name:str, num:int, total:int):
    sys.stdout.write('\r')
    sys.stdout.write("%s %d/%d | %s %s%%" % (name, num, total, int(num / total * 50) * '#', int(num / total * 100)))
    sys.stdout.flush()
    if num == total:
        sys.stdout.write('\n')

class DoctorGraph:
    def __init__(self):
        self.g = Graph(
            # host="120.77.220.71",  # neo4j 搭载服务器的ip地址，ifconfig可获取到
            host="34.92.13.105",
            http_port=7474,        # neo4j 服务器监听的端口号
            user="neo4j",          # 数据库user name，如果没有更改过，应该是neo4j
            password="302899")

        self.mongodb = self.conn = pymongo.MongoClient()
        self.db = self.conn['disease_doctor']
        self.region = ['北京','上海','广东','广西','江苏',
                       '浙江','安徽','江西','福建','山东',
                       '山西','河北','河南','天津','辽宁',
                       '黑龙江','吉林','湖北','湖南','四川',
                       '重庆','山西','甘肃','云南','新疆',
                       '内蒙古','海南','贵州','青海','宁夏','西藏'
                       ]

        self.build()

    def loop_up_table_index(self):
        return self.db.list_collection_names(session=None)

    def look_up(self):
        tables = self.loop_up_table_index()

    def look_up_table(self, tablename):
        table = self.db[tablename]
        return table.find()

    def get_doctor_info(self):
        tablenames = self.loop_up_table_index()
        records = []
        for tname in tablenames:
            for record in self.look_up_table(tname):
                records.append(record)
        return records

    # 创建关系
    def create_relationship(self, start_node, end_node, edges, rel_type, rel_name, seg1, seg2):
        count = 0
        # 去重处理
        set_edges = []
        for edge in edges:
            set_edges.append('###'.join(edge))
        all = len(set(set_edges))
        for edge in set(set_edges):
            edge = edge.split('###')
            p = edge[0]
            q = edge[1]
            query = ("match(p:%s),(q:%s) where p."+seg1+"='%s'and q."+seg2+"='%s' create (p)-[rel:%s{rel_name:'%s'}]->(q)") % (
                start_node, end_node, p, q, rel_type, rel_name)
            try:
                count += 1
                # if count < 170804 or count >= 200000:
                #     continue
                self.g.run(query)
                printBar(start_node+"->"+end_node,count,all)
            except Exception as e:
                print(e)
        return


    # 建立节点
    def create_node(self, label, nodes):
        exists_node = [ n['node.name'] for n in self.g.run("match (node:" + label + ")return node.name").data()]
        timestamp = str(time.time())
        for node_name in nodes:
            if node_name in exists_node:
                continue
            node = Node(label, name=node_name, timestamp=timestamp)
            self.g.create(node)
        return


    def create_region_node(self):
        self.create_node('region', self.region)
        print("创建地域节点完成")

    def create_hospital_node(self):
        hospitals = {}
        records = self.get_doctor_info()
        for i in range(len(records)):
            record = records[i]
            if record['hospital'] not in hospitals:
                self.create_node('hospital', [record['hospital']])
                hospitals[record['hospital']] = 1
            printBar("读入医院",i, total=len(records))

        print("创建医院节点完成")

    def create_doctor_node(self):
        timestamp = str(time.time())
        doctors = self.get_doctor_info()
        total = len(doctors)
        doctorkey = []
        # 建议这里改成多线程，不然太慢了。本次实验我是手动多线程了
        count = 0
        for i in range(count,total):
            count += 1
            doctor = doctors[i]
            if doctor['key'] not in doctorkey:
                doctorkey.append(doctor['key'])
                node = Node("doctor", name=doctor['name'], timestamp=timestamp, region=doctor['region'],
                            key=doctor['key'], title=doctor['title'], hospital=doctor['hospital'],
                            goodat=doctor['goodat'], introduce=doctor['introduce']
                            )
                self.g.create(node)
            printBar("创建医生节点",count,total)
        print("创建医生节点完成,共有医生实体",len(doctorkey))


    def relation_disease_doctor(self):
        diseases = self.loop_up_table_index()
        total = len(diseases)
        option_doctor = []
        diagnose = []
        exist = {}
        for i in range(total):
            disease = diseases[i]
            for record in self.look_up_table(disease):
                if (disease+record['key']) not in exist:
                    exist[disease+record['key']] = 0
                    option_doctor.append([disease,record['key']])
                    diagnose.append([record['key'],disease])
            printBar("读入数据",i,total)

        # self.create_relationship('doctor', 'Disease', diagnose, 'diagnose', '治疗', 'key', 'name')
        self.create_relationship('Disease', 'doctor', option_doctor, 'specialist', '可选医生', 'name', 'key')

    def build(self):
        # pass
        # self.create_region_node() # 创建地域节点
        # self.create_hospital_node()  # 创建医院节点
        self.relation_disease_doctor() # 创建疾病与医生的关系


    # 导出医生名字节点
    def export_doctor(self):
        doctors = self.get_doctor_info()
        dname_map = {}
        with open('./dict/doctor.txt','w+') as f:
            total = len(doctors)
            for i in range(total):
                d = doctors[i]
                if d['name'] not in dname_map:
                    f.write(d['name']+"\n")
                    dname_map[d['name']] = 0
                printBar("导出医生节点",i,total)

    # 导出医院名字节点
    def export_hospital(self):
        doctors = self.get_doctor_info()
        dname_map = {}
        with open('./dict/hospital.txt', 'w+') as f:
            total = len(doctors)
            for i in range(total):
                d = doctors[i]
                if d['hospital'] not in dname_map:
                    f.write(d['hospital'] + "\n")
                    dname_map[d['hospital']] = 0
                printBar("导出医院节点", i, total)




if __name__ == '__main__':
    d = DoctorGraph()

    # d.export_hospital()
    # d.create_doctor_node()

    # d.relation_disease_doctor()






