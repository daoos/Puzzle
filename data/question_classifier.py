import ahocorasick

class QuestionClassifier:
    def __init__(self):
        print("模型初始化中...")
        self.disease_wds= [word.strip() for word in open('./dict/disease.txt') if word.strip()]
        self.department_wds= [i.strip() for i in open('./dict/department.txt') if i.strip()]
        self.check_wds= [word.strip() for word in open('./dict/check.txt') if word.strip()]
        self.drug_wds= [word.strip() for word in open('./dict/drug.txt') if word.strip()]
        self.food_wds= [word.strip() for word in open('./dict/food.txt') if word.strip()]
        self.producer_wds= [word.strip() for word in open('./dict/producer.txt') if word.strip()]
        self.symptom_wds= [word.strip() for word in open('./dict/symptom.txt') if word.strip()]
        self.doctor_name = [word.strip() for word in open('dict/doctor.txt') if word.strip()]
        self.deny_words = [word.strip() for word in open('./dict/deny.txt') if word.strip()]
        self.province = ['北京', '上海', '广东', '广西', '江苏',
                       '浙江', '安徽', '江西', '福建', '山东',
                       '山西', '河北', '河南', '天津', '辽宁',
                       '黑龙江', '吉林', '湖北', '湖南', '四川',
                       '重庆', '山西', '甘肃', '云南', '新疆',
                       '内蒙古', '海南', '贵州', '青海', '宁夏', '西藏'
                       ]
        self.region_words = set(self.province + self.department_wds + self.disease_wds + self.check_wds + self.drug_wds + self.food_wds + self.producer_wds + self.symptom_wds + self.doctor_name)
        self.region_tree = self.build_actree(list(self.region_words))# 构造AC树
        self.typeDict = self.build_wdtype_dict() # 类型字典
        # 问题意图关键字
        self.symptom_qwds = ['症状', '表征', '现象', '临床现象', '特征', '特点']
        self.cause_qwds = ['原因','成因', '咋样才', '怎样会', '如何会', '为什么', '怎么会', '怎样才', '为啥', '为何', '如何才会', '会造成']
        self.acompany_qwds = ['并发症', '并发', '一起发生', '一并发生', '一起出现', '一并出现', '一同发生', '一同出现', '伴随发生', '伴随', '共现']
        self.food_qwds = ['饮食', '饮用', '吃', '食', '伙食', '膳食', '喝', '菜' ,'忌口', '补品', '保健品', '食谱', '菜谱', '食用', '食物','补品']
        self.drug_qwds = ['药', '药品', '药片', '好的药', '用药', '胶囊']
        self.prevent_qwds = ['预防', '防范', '抵制', '抵御','怎样才能不', '如何才能不', '怎么才能不']
        self.lasttime_qwds = ['周期', '多久', '多长时间', '多少时间', '几天', '几年', '多少天', '多少小时', '几个小时', '多少年']
        self.cureway_qwds = ['怎么治疗', '如何医治', '怎么医治', '怎么治', '怎么医', '如何治', '医治方式', '疗法', '咋治', '怎么办', '咋办', '咋治']
        self.cureprob_qwds = ['多大概率能治好', '多大几率能治好', '治好希望大么', '几率', '几成', '比例', '可能性', '能治', '可治', '可以治', '可以医']
        self.easyget_qwds = ['易感人群', '容易感染', '易发人群', '什么人', '哪些人', '感染', '染上', '得上']
        self.check_qwds = ['检查', '检查项目', '查出', '检查', '测出', '试出']
        self.cure_qwds = ['治啥', '治疗什么', '治疗啥', '医治啥', '治愈啥', '主治啥', '主治什么', '有什么用', '有何用', '用处', '用途','有什么好处', '有什么益处', '有何益处', '用来', '用来做啥', '用来作甚']
        self.recommend_doctor = ['推荐的医生','医生推荐','推荐医生','专家','大夫','求推荐医生','推荐一下','好医生','好的医生','这方面的专家']
        self.how_doctor_qwds = ['医生怎么样','资料','介绍','背景','简介','擅长']

        print('初始化完成...')

        return

    # 意图提取 AC
    def classify(self, question):
        data = {}
        medical_dict = self.check_medical(question)
        if not medical_dict:
            return {}

        print('medical_dict',medical_dict)

        data['args'] = medical_dict
        #收集问句当中所涉及到的实体类型
        types = []
        for type_ in medical_dict.values():
            types += type_

        question_types = []

        print('types',types)

        # 医生情况介绍
        if self.check_words(self.how_doctor_qwds, question) and ('doctor' in types):
            question_type = 'ask_how_doctor'
            question_types.append(question_type)

        # 根据疾病推荐医生
        if self.check_words(self.recommend_doctor, question) and ('disease' in types):
            question_type = 'recommend_doctor'
            question_types.append(question_type)

        # 症状
        if self.check_words(self.symptom_qwds, question) and ('disease' in types):
            question_type = 'disease_symptom'
            question_types.append(question_type)

        if self.check_words(self.symptom_qwds, question) and ('symptom' in types):
            question_type = 'symptom_disease'
            question_types.append(question_type)

        # 原因
        if self.check_words(self.cause_qwds, question) and ('disease' in types):
            question_type = 'disease_cause'
            question_types.append(question_type)
        # 并发症
        if self.check_words(self.acompany_qwds, question) and ('disease' in types):
            question_type = 'disease_acompany'
            question_types.append(question_type)

        # 推荐食品
        if self.check_words(self.food_qwds, question) and 'disease' in types:
            deny_status = self.check_words(self.deny_words, question)
            question_type = 'disease_not_food' if deny_status else 'disease_do_food'
            question_types.append(question_type)

        #已知食物找疾病
        if self.check_words(self.food_qwds+self.cure_qwds, question) and 'food' in types:
            deny_status = self.check_words(self.deny_words, question)
            question_type = 'food_not_disease' if deny_status else 'food_do_disease'
            question_types.append(question_type)

        # 推荐药品
        if self.check_words(self.drug_qwds, question) and 'disease' in types:
            question_type = 'disease_drug'
            question_types.append(question_type)

        # 药品治啥病
        if self.check_words(self.cure_qwds, question) and 'drug' in types:
            question_type = 'drug_disease'
            question_types.append(question_type)

        # 疾病接受检查项目
        if self.check_words(self.check_qwds, question) and 'disease' in types:
            question_type = 'disease_check'
            question_types.append(question_type)

        # 已知检查项目查相应疾病
        if self.check_words(self.check_qwds+self.cure_qwds, question) and 'check' in types:
            question_type = 'check_disease'
            question_types.append(question_type)

        #　症状防御
        if self.check_words(self.prevent_qwds, question) and 'disease' in types:
            question_type = 'disease_prevent'
            question_types.append(question_type)

        # 疾病医疗周期
        if self.check_words(self.lasttime_qwds, question) and 'disease' in types:
            question_type = 'disease_lasttime'
            question_types.append(question_type)

        # 疾病治疗方式
        if self.check_words(self.cureway_qwds, question) and 'disease' in types:
            question_type = 'disease_cureway'
            question_types.append(question_type)

        # 疾病治愈可能性
        if self.check_words(self.cureprob_qwds, question) and 'disease' in types:
            question_type = 'disease_cureprob'
            question_types.append(question_type)

        # 疾病易感染人群
        if self.check_words(self.easyget_qwds, question) and 'disease' in types :
            question_type = 'disease_easyget'
            question_types.append(question_type)

        # 若没有查到相关的外部查询信息，那么则将该疾病的描述信息返回
        if question_types == [] and 'disease' in types:
            question_types = ['disease_desc']

        # 若没有查到相关的外部查询信息，那么则将该疾病的描述信息返回
        if question_types == [] and 'symptom' in types:
            question_types = ['symptom_disease']

        # 将多个分类结果进行合并处理，组装成一个字典
        data['question_types'] = question_types

        return data

    '''构造词对应的类型'''
    def build_wdtype_dict(self):
        wd_dict = dict()
        for word in self.region_words:
            wd_dict[word] = []
            if word in self.disease_wds:
                wd_dict[word].append('disease')
            if word in self.department_wds:
                wd_dict[word].append('department')
            if word in self.check_wds:
                wd_dict[word].append('check')
            if word in self.drug_wds:
                wd_dict[word].append('drug')
            if word in self.food_wds:
                wd_dict[word].append('food')
            if word in self.symptom_wds:
                wd_dict[word].append('symptom')
            if word in self.producer_wds:
                wd_dict[word].append('producer')
            if word in self.doctor_name:
                wd_dict[word].append('doctor')
            if word in self.province:
                wd_dict[word].append('province')
        return wd_dict

    '''构造actree，加速过滤'''
    def build_actree(self, wordlist):
        actree = ahocorasick.Automaton()
        for index, word in enumerate(wordlist):
            actree.add_word(word, (index, word))
        actree.make_automaton()
        return actree

    '''问句过滤'''
    def check_medical(self, question):
        region_wds = []
        for i in self.region_tree.iter(question):
            wd = i[1][1]
            region_wds.append(wd)
        stop_wds = []
        for wd1 in region_wds:
            for wd2 in region_wds:
                if wd1 in wd2 and wd1 != wd2:
                    stop_wds.append(wd1)

        print('stop_wds', stop_wds)
        final_wds = [i for i in region_wds if i not in stop_wds]
        print('final_wds',final_wds)
        final_dict = {i:self.typeDict.get(i) for i in final_wds}
        print(final_dict)
        return final_dict

    '''基于特征词进行分类'''
    def check_words(self, wds, sent):
        for wd in wds:
            if wd in sent:
                return True
        return False



if __name__ == '__main__':
    handler = QuestionClassifier()
    while 1:
        question = input('input an question:')
        data = handler.classify(question)
        print(data)

