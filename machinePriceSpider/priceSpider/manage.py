from app import app
from flask_cors import CORS


if __name__ == '__main__':
	app.config['JSON_AS_ASCII'] = False
	CORS(app, resources=r'/*')
	app.run()
# app.run(host="0.0.0.0", port=5000)
