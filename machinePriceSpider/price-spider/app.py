from flask import Flask, request
from flask_cors import *

app = Flask(__name__)
CORS(app, resource=r'/*')
