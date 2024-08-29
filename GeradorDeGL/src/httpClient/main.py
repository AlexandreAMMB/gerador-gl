from flask import Flask, request, jsonify
import spacy

nlp = spacy.load("pt_core_news_sm")

app = Flask(__name__)

@app.route('/pos_tagging', methods=['POST'])
def pos_tagging():
    text = request.json['text']
    doc = nlp(text)
    pos_tags = ""
    for token in doc:
        pos_tags += token.pos_ + ", "
    pos_tags = pos_tags.rstrip(", ")
    print(pos_tags)
    response = jsonify(pos_tags)
    response.headers['Content-Type'] = 'application/json; charset=utf-8'
    return response

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)