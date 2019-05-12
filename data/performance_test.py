import urllib.request
import urllib.parse
from py2neo import Graph

class driver:
    '''根据url，请求html'''
    def get_html(self, url):
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) '
                                 'Chrome/51.0.2704.63 Safari/537.36'}
        req = urllib.request.Request(url=url, headers=headers)
        res = urllib.request.urlopen(req)
        html = res.read()
        return html

    def __init__(self):
        self.g = Graph(
            # host="120.77.220.71",  # shenzhen
            host="35.236.82.226",  # losangel
            # host="34.92.13.105", # hongkong
            # host="35.236.82.226",  # taiwan
            http_port=7474,
            user="neo4j",
            password="302899")


import time

total_time = 0
d = driver()

for i in range(10):
    print(i)
    t0 = time.time()
    # # driver().get_html("http://localhost:8080/neo4j/%E7%99%BE%E6%97%A5%E5%92%B3") # 百日咳
    # driver().get_html( "http://35.236.82.226:8080/neo4j/%E8%89%BE%E6%BB%8B%E7%97%85")
    d.g.run("match (n:Disease) where n.name=\"癌症\" return n").data()
    total_time += time.time() - t0

print(total_time)
