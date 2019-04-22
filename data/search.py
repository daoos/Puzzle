from py2neo import Graph
import ahocorasick


class Searcher:
    def __init__(self):
        self.g = Graph(
            host="120.77.220.71",
            http_port=7474,
            user="neo4j",
            password="302899")
        self.num_limit = 20

    # 生成sql模板进入neo4j 查询
    def search_sql(self, sqls):
        final_answers = []
        for sql_ in sqls:
            question_type = sql_['question_type']
            queries = sql_['sql']
            answers = []
            for query in queries:
                ress = self.g.run(query).data()
                answers += ress
                print(query,ress)
            final_answer = self.get_answer(question_type, answers)
            if final_answer:
                final_answers.append(final_answer)
        return final_answers

    # 根据问题类型，搜索答案
    def get_answer(self, questionLabel, answers):
        final_answer = ""
        if not answers:
            return ''
        if questionLabel == 'disease_symptom':
            desc = [i['n.name'] for i in answers]
            subject = answers[0]['m.name']
            final_answer = '{0}的症状包括：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'disease_cureway':
            desc = [';'.join(i['m.cure_way']) for i in answers]
            subject = answers[0]['m.name']
            final_answer = '{0}有以下治疗方法：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'disease_cureprob':
            desc = [i['m.cured_prob'] for i in answers]
            subject = answers[0]['m.name']
            final_answer = '{0}治愈的概率为：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'disease_lasttime':
            desc = [i['m.cure_lasttime'] for i in answers]
            subject = answers[0]['m.name']
            final_answer = '{0}治疗治疗周期为：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))


        elif questionLabel == 'disease_desc':
            desc = [i['m.desc'] for i in answers]
            subject = answers[0]['m.name']
            final_answer = '{0}科普介绍：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'disease_acompany':
            desc1 = [i['n.name'] for i in answers]
            desc2 = [i['m.name'] for i in answers]
            subject = answers[0]['m.name']
            desc = [i for i in desc1 + desc2 if i != subject]
            final_answer = '{0}的症状包括：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'disease_not_food':
            desc = [i['n.name'] for i in answers]
            subject = answers[0]['m.name']
            final_answer = '{0}忌食的食物包括有：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'disease_do_food':
            do_desc = [i['n.name'] for i in answers if i['r.name'] == '宜吃']
            recommand_desc = [i['n.name'] for i in answers if i['r.name'] == '推荐食谱']
            subject = answers[0]['m.name']
            final_answer = '{0}患者推荐的食物包括有：{1}\n推荐食谱包括有：{2}'.format(subject, ';'.join(list(set(do_desc))[:self.num_limit]),
                                                                 ';'.join(list(set(recommand_desc))[:self.num_limit]))

        elif questionLabel == 'food_not_disease':
            desc = [i['m.name'] for i in answers]
            subject = answers[0]['n.name']
            final_answer = '{0}患者忌口食物有{1}'.format('；'.join(list(set(desc))[:self.num_limit]), subject)


        elif questionLabel == 'disease_easyget':
            desc = [i['m.easy_get'] for i in answers]
            subject = answers[0]['m.name']

            final_answer = '{0}的易感人群有：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'symptom_disease':
            desc = [i['m.name'] for i in answers]
            subject = answers[0]['n.name']
            final_answer = '症状{0}可能染上的疾病有：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'disease_cause':
            desc = [i['m.cause'] for i in answers]
            subject = answers[0]['m.name']
            final_answer = '{0}可能的成因有：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'disease_prevent':
            desc = [i['m.prevent'] for i in answers]
            subject = answers[0]['m.name']
            final_answer = '{0}的预防措施包括：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'food_do_disease':
            desc = [i['m.name'] for i in answers]
            subject = answers[0]['n.name']
            final_answer = '{0}推荐食物有{1}'.format('；'.join(list(set(desc))[:self.num_limit]), subject)

        elif questionLabel == 'disease_drug':
            desc = [i['n.name'] for i in answers]
            subject = answers[0]['m.name']
            final_answer = '{0}推荐药品有：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'drug_disease':
            desc = [i['m.name'] for i in answers]
            subject = answers[0]['n.name']
            final_answer = '{0}治疗的疾病有{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'disease_check':
            desc = [i['n.name'] for i in answers]
            subject = answers[0]['m.name']
            final_answer = '{0}通常可以通过以下方式检查出来：{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'check_disease':
            desc = [i['m.name'] for i in answers]
            subject = answers[0]['n.name']
            final_answer = '通常可以通过{0}检查出来的疾病有{1}'.format(subject, '；'.join(list(set(desc))[:self.num_limit]))

        elif questionLabel == 'ask_how_doctor':
            intro = [i['d.introduce'] for i in answers]
            final_answer = intro[0]

        elif questionLabel == 'recommend_doctor':
            print('该地区推荐的医生有：')
            i=0
            for record in answers:
                final_answer += str(i+1) + ". " + record['d.key'] + "\n"
                if i > 10: # 最多显示十个医生
                    break
                i+=1


        print(final_answer)
        return final_answer

class Intent_Recognization:
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
        self.typeDict = self.word_type_index() # 类型字典
        # 问题意图关键字
        self.symptom_words = ['症状', '表征', '现象', '临床现象', '特征', '特点']
        self.cause_wrods = ['原因', '成因', '咋样才', '怎样会', '如何会', '为什么', '怎么会', '怎样才', '为啥', '为何', '如何才会', '会造成']
        self.concurrent_words = ['并发症', '并发', '一起发生', '一并发生', '一起出现', '一并出现', '一同发生', '一同出现', '伴随发生', '伴随', '共现']
        self.food_words = ['饮食', '饮用', '吃', '食', '伙食', '膳食', '喝', '菜' , '忌口', '补品', '保健品', '食谱', '菜谱', '食用', '食物', '补品']
        self.drug_words = ['药', '药品', '药片', '好的药', '用药', '胶囊']
        self.prevent_words = ['预防', '防范', '抵制', '抵御', '怎样才能不', '如何才能不', '怎么才能不']
        self.lasttime_words = ['周期', '多久', '多长时间', '多少时间', '几天', '几年', '多少天', '多少小时', '几个小时', '多少年']
        self.cureway_words = ['怎么治疗', '如何医治', '怎么医治', '怎么治', '怎么医', '如何治', '医治方式', '疗法', '咋治', '怎么办', '咋办', '咋治']
        self.cureprob_words = ['多大概率能治好', '多大几率能治好', '治好希望大么', '几率', '几成', '比例', '可能性', '能治', '可治', '可以治', '可以医']
        self.easyget_words = ['易感人群', '容易感染', '易发人群', '什么人', '哪些人', '感染', '染上', '得上']
        self.check_words = ['检查', '检查项目', '查出', '检查', '测出', '试出']
        self.cure_words = ['治啥', '治疗什么', '治疗啥', '医治啥', '治愈啥', '主治啥', '主治什么', '有什么用', '有何用', '用处', '用途', '有什么好处', '有什么益处', '有何益处', '用来', '用来做啥', '用来作甚']
        self.recommend_doctor = ['推荐的医生','医生推荐','推荐医生','专家','大夫','求推荐医生','推荐一下','好医生','好的医生','这方面的专家']
        self.how_doctor_words = ['医生怎么样', '资料', '介绍', '背景', '简介', '擅长', '医院']

        print('初始化完成...')

        return

    # 意图提取 AC
    def classify(self, question):
        data = {}
        medical_dict = self.label_keyword(question)
        if medical_dict is None:
            return {}

        print('medical_dict',medical_dict)

        data['args'] = medical_dict
        #收集问句当中所涉及到的实体类型
        labels = []
        questionLabels = []
        for label in medical_dict.values():
            labels += label

        print('types',labels)

        # 医生情况介绍
        if self.check_words(self.how_doctor_words, question) and ('doctor' in labels):
            question_label = 'ask_how_doctor'
            questionLabels.append(question_label)

        # 根据疾病推荐医生
        if self.check_words(self.recommend_doctor, question) and ('disease' in labels):
            question_label = 'recommend_doctor'
            questionLabels.append(question_label)

        # 疾病症状咨询
        if self.check_words(self.symptom_words, question) and ('disease' in labels):
            question_label = 'disease_symptom'
            questionLabels.append(question_label)

        # 疾病成因咨询
        if self.check_words(self.cause_wrods, question) and ('disease' in labels):
            question_label = 'disease_cause'
            questionLabels.append(question_label)

        # 疾病并发症咨询
        if self.check_words(self.concurrent_words, question) and ('disease' in labels):
            question_label = 'disease_acompany'
            questionLabels.append(question_label)

        # 疾病推荐因素
        if self.check_words(self.food_words, question) and 'disease' in labels:
            deny_status = self.check_words(self.deny_words, question)
            question_label = 'disease_not_food' if deny_status else 'disease_do_food'
            questionLabels.append(question_label)

        # 疾病推荐药品
        if self.check_words(self.drug_words, question) and 'disease' in labels:
            question_label = 'disease_drug'
            questionLabels.append(question_label)

        # 最药品的咨询
        if self.check_words(self.cure_words, question) and 'drug' in labels:
            question_label = 'drug_disease'
            questionLabels.append(question_label)

        # 疾病检查咨询
        if self.check_words(self.check_words, question) and 'disease' in labels:
            question_label = 'disease_check'
            questionLabels.append(question_label)

        # 已知检查项目查相应疾病
        if self.check_words(self.check_words + self.cure_words, question) and 'check' in labels:
            question_label = 'check_disease'
            questionLabels.append(question_label)

        # 疾病预防信息咨询
        if self.check_words(self.prevent_words, question) and 'disease' in labels:
            question_label = 'disease_prevent'
            questionLabels.append(question_label)

        # 疾病治疗周期咨询
        if self.check_words(self.lasttime_words, question) and 'disease' in labels:
            question_label = 'disease_lasttime'
            questionLabels.append(question_label)

        # 疾病治疗方式
        if self.check_words(self.cureway_words, question) and 'disease' in labels:
            question_label = 'disease_cureway'
            questionLabels.append(question_label)

        # 疾病治愈可能性
        if self.check_words(self.cureprob_words, question) and 'disease' in labels:
            question_label = 'disease_cureprob'
            questionLabels.append(question_label)

        # 疾病易感染人群
        if self.check_words(self.easyget_words, question) and 'disease' in labels :
            question_label = 'disease_easyget'
            questionLabels.append(question_label)

        # 若没有查到相关的外部查询信息，那么则将该疾病的描述信息返回
        if questionLabels == [] and 'disease' in labels:
            questionLabels = ['disease_desc']

        # 若没有查到相关的外部查询信息，那么则将该疾病的描述信息返回
        if questionLabels == [] and 'symptom' in labels:
            questionLabels = ['symptom_disease']

        # 将多个分类结果进行合并处理，组装成一个字典
        data['questionLabels'] = questionLabels

        return data

    # 内存类型字典，label word
    def word_type_index(self):
        typeDict = {}
        for word in self.region_words:
            typeDict[word] = []
            if word in self.disease_wds:
                typeDict[word].append('disease')
            if word in self.department_wds:
                typeDict[word].append('department')
            if word in self.check_wds:
                typeDict[word].append('check')
            if word in self.drug_wds:
                typeDict[word].append('drug')
            if word in self.food_wds:
                typeDict[word].append('food')
            if word in self.symptom_wds:
                typeDict[word].append('symptom')
            if word in self.producer_wds:
                typeDict[word].append('producer')
            if word in self.doctor_name:
                typeDict[word].append('doctor')
            if word in self.province:
                typeDict[word].append('province')
        return typeDict


    # 根据wordlist 构造AC自动机模型
    def build_actree(self, wordlist):
        actree = ahocorasick.Automaton()
        for index, word in enumerate(wordlist):
            actree.add_word(word, (index, word))
        actree.make_automaton()
        return actree

    # 从问句中提出关键字并标注类型
    def label_keyword(self, question):
        region_wds = []
        for i in self.region_tree.iter(question):
            wd = i[1][1]
            region_wds.append(wd)
        stop_wds = []
        for w1 in region_wds:
            for w2 in region_wds:
                if w1 in w2 and w1 != w2:
                    stop_wds.append(w1)

        print('stop_wds', stop_wds)
        final_wds = [i for i in region_wds if i not in stop_wds]
        print('final_wds',final_wds)
        final_dict = {i:self.typeDict.get(i) for i in final_wds}
        print(final_dict)
        return final_dict

    # 查看问句中是否有关键词
    def check_words(self, region_words, qustion):
        for word in region_words:
            if word in qustion:
                return True
        return False

class SQL_Generator:

    # 构建实体词典
    def build_entitydict(self, args):
        entity_dict = {}
        for arg, types in args.items():
            for type in types:
                if type not in entity_dict:
                    entity_dict[type] = [arg]
                else:
                    entity_dict[type].append(arg)

        print('entity_dict',entity_dict)
        return entity_dict

    # 生成sql搜索模板
    def generate_sql(self, res_classify):
        args = res_classify['args']
        entity_dict = self.build_entitydict(args)
        question_types = res_classify['questionLabels']
        sqls = []
        for question_type in question_types:
            sql_ = {}
            sql_['question_type'] = question_type
            sql = []
            if question_type == 'disease_symptom':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'symptom_disease':
                sql = self.sql_transfer(question_type, entity_dict.get('symptom'))

            elif question_type == 'disease_cause':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'disease_acompany':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'disease_not_food':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'disease_do_food':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'food_not_disease':
                sql = self.sql_transfer(question_type, entity_dict.get('food'))

            elif question_type == 'food_do_disease':
                sql = self.sql_transfer(question_type, entity_dict.get('food'))

            elif question_type == 'disease_drug':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'drug_disease':
                sql = self.sql_transfer(question_type, entity_dict.get('drug'))

            elif question_type == 'disease_check':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'check_disease':
                sql = self.sql_transfer(question_type, entity_dict.get('check'))

            elif question_type == 'disease_prevent':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'disease_lasttime':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'disease_cureway':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'disease_cureprob':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'disease_easyget':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'disease_desc':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'))

            elif question_type == 'ask_how_doctor':
                sql = self.sql_transfer(question_type, entity_dict.get('doctor'))

            elif question_type == 'recommend_doctor':
                sql = self.sql_transfer(question_type, entity_dict.get('disease'), limit=entity_dict.get('province'))

            if sql:
                sql_['sql'] = sql
                sqls.append(sql_)

        return sqls

    # 根据问题类型嵌套模板
    def sql_transfer(self, question_type, entities, limit=None):
        if not entities:
            return []

        # 查询语句
        sql = []
        # 查询疾病的原因
        if question_type == 'disease_cause':
            sql = ["MATCH (m:Disease) where m.name = '{0}' return m.name, m.cause".format(i) for i in entities]

        # 查询疾病的防御措施
        elif question_type == 'disease_prevent':
            sql = ["MATCH (m:Disease) where m.name = '{0}' return m.name, m.prevent".format(i) for i in entities]

        # 查询疾病的持续时间
        elif question_type == 'disease_lasttime':
            sql = ["MATCH (m:Disease) where m.name = '{0}' return m.name, m.cure_lasttime".format(i) for i in entities]

        # 查询疾病的治愈概率
        elif question_type == 'disease_cureprob':
            sql = ["MATCH (m:Disease) where m.name = '{0}' return m.name, m.cured_prob".format(i) for i in entities]

        # 查询疾病的治疗方式
        elif question_type == 'disease_cureway':
            sql = ["MATCH (m:Disease) where m.name = '{0}' return m.name, m.cure_way".format(i) for i in entities]

        # 查询疾病的易发人群
        elif question_type == 'disease_easyget':
            sql = ["MATCH (m:Disease) where m.name = '{0}' return m.name, m.easy_get".format(i) for i in entities]

        # 查询疾病的相关介绍
        elif question_type == 'disease_desc':
            sql = ["MATCH (m:Disease) where m.name = '{0}' return m.name, m.desc".format(i) for i in entities]

        # 查询疾病有哪些症状
        elif question_type == 'disease_symptom':
            sql = ["MATCH (m:Disease)-[r:has_symptom]->(n:Symptom) where m.name = '{0}' return m.name, r.name, n.name".format(i) for i in entities]

        # 查询症状会导致哪些疾病
        elif question_type == 'symptom_disease':
            sql = ["MATCH (m:Disease)-[r:has_symptom]->(n:Symptom) where n.name = '{0}' return m.name, r.name, n.name".format(i) for i in entities]

        # 查询疾病的并发症
        elif question_type == 'disease_acompany':
            sql1 = ["MATCH (m:Disease)-[r:acompany_with]->(n:Disease) where m.name = '{0}' return m.name, r.name, n.name".format(i) for i in entities]
            sql2 = ["MATCH (m:Disease)-[r:acompany_with]->(n:Disease) where n.name = '{0}' return m.name, r.name, n.name".format(i) for i in entities]
            sql = sql1 + sql2
        # 查询疾病的忌口
        elif question_type == 'disease_not_food':
            sql = ["MATCH (m:Disease)-[r:no_eat]->(n:Food) where m.name = '{0}' return m.name, r.name, n.name".format(i) for i in entities]

        # 查询疾病建议吃的东西
        elif question_type == 'disease_do_food':
            sql1 = ["MATCH (m:Disease)-[r:do_eat]->(n:Food) where m.name = '{0}' return m.name, r.name, n.name".format(i) for i in entities]
            sql2 = ["MATCH (m:Disease)-[r:recommand_eat]->(n:Food) where m.name = '{0}' return m.name, r.name, n.name".format(i) for i in entities]
            sql = sql1 + sql2

        # 已知忌口查疾病
        elif question_type == 'food_not_disease':
            sql = ["MATCH (m:Disease)-[r:no_eat]->(n:Food) where n.name = '{0}' return m.name, r.name, n.name".format(i) for i in entities]

        # 已知推荐查疾病
        elif question_type == 'food_do_disease':
            sql1 = ["MATCH (m:Disease)-[r:do_eat]->(n:Food) where n.name = '{0}' return m.name, r.name, n.name".format(i) for i in entities]
            sql2 = ["MATCH (m:Disease)-[r:recommand_eat]->(n:Food) where n.name = '{0}' return m.name, r.name, n.name".format(i) for i in entities]
            sql = sql1 + sql2

        # 查询疾病常用药品－药品别名记得扩充
        elif question_type == 'disease_drug':
            sql1 = ["MATCH (m:Disease)-[r:common_drug]->(n:Drug) where m.name = '{0}' " \
                    "return m.name, r.name, n.name".format(i) for i in entities]
            sql2 = ["MATCH (m:Disease)-[r:recommand_drug]->(n:Drug) where m.name = '{0}' " \
                    "return m.name, r.name, n.name".format(i) for i in entities]
            sql = sql1 + sql2

        # 已知药品查询能够治疗的疾病
        elif question_type == 'drug_disease':
            sql1 = ["MATCH (m:Disease)-[r:common_drug]->(n:Drug) where n.name = '{0}' " \
                    "return m.name, r.name, n.name".format(i) for i in entities]
            sql2 = ["MATCH (m:Disease)-[r:recommand_drug]->(n:Drug) where n.name = '{0}' " \
                    "return m.name, r.name, n.name".format(i) for i in entities]
            sql = sql1 + sql2
        # 查询疾病应该进行的检查
        elif question_type == 'disease_check':
            sql = ["MATCH (m:Disease)-[r:need_check]->(n:Check) where m.name = '{0}' " \
                   "return m.name, r.name, n.name".format(i) for i in entities]

        # 已知检查查询疾病
        elif question_type == 'check_disease':
            sql = ["MATCH (m:Disease)-[r:need_check]->(n:Check) where n.name = '{0}' " \
                   "return m.name, r.name, n.name".format(i) for i in entities]

        # 询问医生相关信息
        elif question_type == 'ask_how_doctor':
            sql = ["MATCH (d:doctor) where d.name='"+entities[0]+"' return d.introduce"]

        # 询问疾病的推荐医生
        elif question_type == 'recommend_doctor':
            sql = ["MATCH (d:doctor)-[r]-(e:Disease) where e.name='{0}' and d.region='{1}' "
                   "return d.key,d.hospital,d.introduce".format(entities[0],limit[0])]

        return sql

