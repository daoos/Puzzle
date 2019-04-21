from question_classifier import *
from question_parser import *
from answer_search import *

'''问答类'''
class ChatBotGraph:
    def __init__(self):
        self.classifier = QuestionClassifier()
        self.parser = QuestionPaser()
        self.searcher = AnswerSearcher()

    def chat_main(self, sent):
        answer = '请输入问题'
        res_classify = self.classifier.classify(sent)
        print('res_classify', res_classify)
        if not res_classify:
            return "你这个问题我回答不出来，请问的简单些。比如白血病是什么，白血病能治吗？谢谢，我是智障。"
        res_sql = self.parser.parser_main(res_classify)
        print('res_sql',res_sql)
        final_answers = self.searcher.search_main(res_sql)
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

