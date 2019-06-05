# -*- coding: UTF-8 -*-

import requests
import sys
import warnings
warnings.filterwarnings("ignore")

# data = '{"q":"艾滋病是什么", "project":"default", "model": "model_20190604-120902"}'
#
# response = requests.post('http://127.0.0.1:5000/parse', data=data.encode('utf-8')).json()
#
# print(response)

class Rasa():
    def __init__(self, url, port, model, project="default"):
        self.url = "http://" + str(url) + ":"+ str(port) + "/parse"
        self.model = model
        self.port = port
        self.project = project

    def getResponse(self,question):
        try:
            data = '{"q":"%s", "project":"%s", "model":"%s"}' % (question, self.project, self.model)
            return requests.post(self.url, data=data.encode('utf-8')).json()
        except:
            return None

    def display(self, question):
        response = self.getResponse(question)
        if response is None:
            print("Exception")
            return
        print(response['intent'],)
        print()
        for ir in response['intent_ranking']:
            print('%s: %.5f' % (ir['name'], ir['confidence']))

if __name__ == '__main__':
    rasa = Rasa("127.0.0.1", 5000, "model_20190604-120902", "default")
    rasa.display(sys.argv[1])

# print(rasa.getResponse("艾滋病是什么"))