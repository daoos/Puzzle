from search import *

'''问答类'''
class ChatBotGraph:
    def __init__(self):
        self.Intent_Recognization = Intent_Recognization()
        self.SQL_Generator = SQL_Generator()
        self.Searcher = Searcher()
        self.province = ['北京', '上海', '广东', '广西', '江苏',
                         '浙江', '安徽', '江西', '福建', '山东',
                         '山西', '河北', '河南', '天津', '辽宁',
                         '黑龙江', '吉林', '湖北', '湖南', '四川',
                         '重庆', '山西', '甘肃', '云南', '新疆',
                         '内蒙古', '海南', '贵州', '青海', '宁夏', '西藏'
                         ]
        self.status = 0
        self.status_dict = {'normal': 0, 'ask_location': 1}
        self.lastquestion = ""


    def chat_main(self, question: str):
        question_label = self.Intent_Recognization.classify(question)

        print('question_label', question_label)

        if not question_label:
            return "你这个问题我回答不出来，请问的简单些。比如白血病是什么，白血病能治吗？谢谢，我是智障。"

        if 'recommend_doctor' in question_label['questionLabels']:
            flag = False
            for province in self.province:
                if province in question:
                    flag = True
                    break
            if not flag:
                return "请同时告诉我您坐在的省份信息，例如北京市有什么小儿感冒医生推荐"

        sql = self.SQL_Generator.generate_sql(question_label)
        print('sql_template',sql)
        final_answers = self.Searcher.search_sql(sql)
        if not final_answers:
            return "你这个问题我回答不出来，请问的简单些。比如白血病是什么，白血病能治吗？谢谢，我是智障。"
        else:
            return '\n'.join(final_answers)


from flask import Flask
app = Flask(__name__)

handler = ChatBotGraph()

@app.route('/<question>',methods=['GET'])
def hello_world(question):
    data = handler.chat_main(question)
    return data

if __name__ == '__main__':
    app.run(host='0.0.0.0')

