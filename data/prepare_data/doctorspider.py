import urllib.request
import urllib.parse
import re
from lxml import etree
import os
import pymongo

class doctorSpider:
    def __init__(self):
        self.conn = pymongo.MongoClient()
        self.db = self.conn['disease_doctor']

        self.disease_class = ['neike', 'waike', 'guwaike', 'zhongyixue', 'zhongyixue', 'zhongliuke',
                         'pifuxingbingke', 'wuguanke', 'erkexue', 'yankexue', 'ganranzhongxin',
                         'kouqiangkexue', 'qitakeshi', 'jingshenxinlike', 'kangfuyixueke','shaoshangke',
                         'mazuiyixueke', 'jieruyixueke', 'pifumeirong', 'zhongxiyijieheke', 'yixueyingxiangke',
                         'shengzhizhongxin', 'nanke', 'yundongyixueke', 'binglike', 'zhiyebingke', 'yingyangke'
                         ]

        self.region = {'beijing':'北京', 'shanghai':'上海', 'guangdong':'广东', 'guangxi':'广西', 'jiangsu':'江苏', 'zhejiang':'浙江',
                       'anhui':'安徽', 'jiangxi':'江西', 'fujian':'福建', 'shandong':'山东', 'shanxi':'山西', 'hebei':'河北', 'henan':'河南',
                       'tianjin':'天津', 'liaoning':'辽宁', 'heilongjiang':'黑龙江', 'jilin':'吉林', 'hubei':'湖北', 'hunan':'湖南', 'sichuan':'四川',
                       'chongqing':'重庆', 'shanxi':'山西', 'gansu':'甘肃', 'yunnan':'云南', 'xinjiang':'新疆', 'neimenggu':'内蒙古', 'hainan':'海南',
                       'guizhou':'贵州', 'qinghai':'青海', 'ningxia':'宁夏', 'xizang':'西藏'
                        }

        self.specialist = {}


    def get_html(self, url):
        '''
        根据url，请求html
        :param url:
        :return:
        '''
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) '
                                 'Chrome/51.0.2704.63 Safari/537.36'}
        req = urllib.request.Request(url=url, headers=headers)
        res = urllib.request.urlopen(req)
        html = res.read().decode('gbk')
        return html


    def get_disease_name_url(self):
        '''
        获得疾病主页的的url
        :return:
        '''
        res = []

        if os.path.exists("disease_index.txt") == True:
            with open("disease_index.txt") as f:
                for line in f.readlines():
                    line = line[:-1]
                    res.append(line.split(","))
            return res

        index_url = "http://z.xywy.com/bzhuanye-"

        count = 0
        f = open("disease_index.txt", "w")
        for disease_c in self.disease_class:
            class_html = self.get_html(index_url + disease_c + ".htm")  # 类型疾病列表
            urls = re.findall(re.compile(r'http[s]?://z.xywy.com/b-.+\.htm'), class_html)
            for url in urls:
                try:
                    selector = etree.HTML(self.get_html(url))
                    name = selector.xpath('//div[@class="z-head-name"]/strong/text()')[0]
                    res.append([name, url])
                    f.write(name+","+url+"\n")
                    print(count, [name, url])
                    count+= 1
                except:
                    continue

        return res


    def get_region_doctor_urls(self, region_doctor_index_url):
        '''
        获取该地区医生的主页url集合
        :param region_doctor_index_url:
        :return:
        '''
        html = self.get_html(region_doctor_index_url)
        return set(re.findall(re.compile(r'http[s]?://z.xywy.com/zhuanjia.+\.htm'), html))


    def get_specialist_info(self, url, region):
        '''
        获取专家信息
        :param url: 
        :return: 
        '''
        html = self.get_html(url)
        selector = etree.HTML(html)
        name = selector.xpath('//div[@class="z-head-name"]/strong/a/text()')[0]
        title = selector.xpath('//div[@class="z-head-name"]/span/text()')[0]
        hospital = selector.xpath('//div[@class="clearfix"]/span[@class="fl"]/a/text()')[0]

        goodat = selector.xpath('//div[@class="fl d-p-infor-con pr  clearfix"]/p[@class="doctor-txt-infor-all none pr fl"]/text()')
        if goodat:
            goodat = goodat[0]
        else:
            goodat = selector.xpath('//div[@class="fl d-p-infor-con pr  clearfix"]/p[@class="doctor-txt-infor fl"]/text()')[0]\
                .replace("\t","").replace("\n","")
        introduce = selector.xpath('//p[@class="doctor-txt-infor-all  none pr fl"]/text()')
        if introduce:
            introduce = introduce[0]
        else:
            introduce = selector.xpath('//p[@class="doctor-txt-infor fl"]/text()')[0].replace("\t","").replace("\n","")
        return {'key': hospital+'-'+name, 'region': region, 'name':name, 'title':title, 'hospital': hospital, 'goodat':goodat, 'introduce':introduce}


    def craw(self):
        disease_name_urls = self.get_disease_name_url()
        n = len(disease_name_urls)
        print("疾病列表采集完毕，共", len(disease_name_urls),"项")
        for i in range(1566,1600):
            disease_name = disease_name_urls[i][0]
            print(i,'/',n, disease_name)
            basic_url = disease_name_urls[i][1].replace('http://z.xywy.com/b-','http://z.xywy.com/bzhuanjia-').replace('.htm','')
            for region in self.region: # 按省份，得到该省名医的列表
                region_doctor_index_url = basic_url + '-' + region + '.htm'
                region_doctor_urls = self.get_region_doctor_urls(region_doctor_index_url)
                print(region, len(region_doctor_urls))
                for specialist_url in list(region_doctor_urls)[:10]:
                    try:
                        # print(disease_name, self.get_specialist_info(specialist_url, self.region[region]))
                        self.db[disease_name].insert(self.get_specialist_info(specialist_url, self.region[region]))
                    except:
                        continue
                        print(specialist_url)













s = doctorSpider()
s.craw()
# print(s.get_disease_name_url())
# print(s.get_specialist_info('http://z.xywy.com/zhuanjia-anzhen-xinzangwaike-anzhenmaobin.htm'))