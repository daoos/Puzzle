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
        if not res_classify:
            return answer
        res_sql = self.parser.parser_main(res_classify)
        final_answers = self.searcher.search_main(res_sql)
        if not final_answers:
            return answer
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

