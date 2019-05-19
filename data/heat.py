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
            host="120.77.220.71",  # shenzhen
            # host="35.236.82.226",  # losangel
            # host="34.92.13.105", # hongkong
            # host="35.236.82.226",  # taiwan
            http_port=7474,
            user="neo4j",
            password="302899")

# disease
disease = [i['n.name'] for i in driver().g.run("match (n:Disease) return n.name").data()]
total = len(disease)
with open("./heat/disease.txt","w+") as f:
    for i in range(total):
        entity = disease[i]
        a = driver().g.run("match (source)-[rela]->(target) where source.name='{0}' or target.name='{0}' return source.name,rela.name,target.name".format(entity)).data()
        s = entity+" "+str(len(a))+"\n"
        print(i, "/", total,"\t",s)
        f.write(s)

# drug = [i['n.name'] for i in driver().g.run("match (n:Drug) return n.name").data()]
# total = len(drug)
# with open("./heat/drug.txt","w") as f:
#     for i in range(total):
#         entity = drug[i]
#         a = driver().g.run("match (source)-[rela]->(target) where source.name='{0}' or target.name='{0}' return source.name,rela.name,target.name".format(entity)).data()
#         s = entity+" "+str(len(a))+"\n"
#         print(i, "/", total,"\t",s)
#         f.write(s)



# print(drug)

