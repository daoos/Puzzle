from search import *

'''问答类'''
class ChatBotGraph:
    def __init__(self):
        self.Intent_Recognization = Intent_Recognization()
        self.SQL_Generator = SQL_Generator()
        self.Searcher = Searcher()

    def chat_main(self, question: str):
        answer = '请输入问题'
        res_classify = self.Intent_Recognization.classify(question)
        print('res_classify', res_classify)
        if not res_classify:
            return "你这个问题我回答不出来，请问的简单些。比如白血病是什么，白血病能治吗？谢谢，我是智障。"
        res_sql = self.SQL_Generator.parser_main(res_classify)
        print('res_sql',res_sql)
        final_answers = self.Searcher.search_main(res_sql)
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

