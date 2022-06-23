from app import app
from flask_cors import CORS
import excel
import product
import login
import access

if __name__ == '__main__':
	app.config['JSON_AS_ASCII'] = False
    app.run(host="0.0.0.0", port=5000)
