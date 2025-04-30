from flask import Flask, request, jsonify
import pandas as pd
import joblib

app = Flask(__name__)

# Load the trained model
try:
    model = joblib.load('maintenance_model.pkl')
except Exception as e:
    print(f"Error loading model: {e}")  # Log the error
    model = None  # Set model to None to prevent further errors

@app.route('/predict', methods=['POST'])
def predict():
    if model is None:
        return jsonify({'error': 'Model not loaded'}), 500  # Internal Server Error

    try:
        data = request.get_json(force=True)
        df = pd.DataFrame([{
            'cout': data['cout'],
            'temperature': data['temperature'],
            'humidite': data['humidite'],
            'consoCarburant': data['consoCarburant'],
            'consoEnergie': data['consoEnergie']
        }])
        prediction = model.predict(df)
        return jsonify({'prediction': prediction[0]}), 200  # Explicit 200 OK
    except KeyError as e:
        return jsonify({'error': f'Missing key in input data: {e}'}), 400  # Bad Request
    except ValueError as e:
        return jsonify({'error': f'Invalid input data: {e}'}), 400  # Bad Request
    except Exception as e:
        return jsonify({'error': f'An error occurred: {e}'}), 500  # Internal Server Error

if __name__ == '__main__':
    app.run(debug=True)

