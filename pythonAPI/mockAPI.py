from flask import Flask, jsonify

app = Flask(__name__)

courses = [
    {
        "id": "E T 483",
        "title": "Mobile App Programming",
        "credits": 4,
        "instructor": "Masadeh",
        "prerequisite": None,
        "tags": ["Kotlin", "Compose", "Android"],
        "instructorImageUrl": ""
    },
    {
        "id": "ICT 458",
        "title": "Web Dev and DB Applications",
        "credits": 4,
        "instructor": "Ball",
        "prerequisite": "Intro To Programming",
        "tags": ["SQL", "noSQL", "Web"],
        "instructorImageUrl": ""
    },
    {
        "id": "ICT 380",
        "title": "Python",
        "credits": 4,
        "instructor": "Braker",
        "prerequisite": None,
        "tags": ["Flask", "Django", "Automation"],
        "instructorImageUrl": "https://et.nmsu.edu/images/Braker.jpg"
    },
    {
        "id": "ICT 145",
        "title": "Network Essentials",
        "credits": 4,
        "instructor": "Ball",
        "prerequisite": "IT Essentials",
        "tags": ["Flask", "Django", "Automation"],
        "instructorImageUrl": ""
    },
    {
        "id": "ICT 220",
        "title": "Discreet Math",
        "credits": 4,
        "instructor": "Braker",
        "prerequisite": None,
        "tags": ["Flask", "Django", "Automation"],
        "instructorImageUrl": "https://et.nmsu.edu/images/Braker.jpg"
    }
]

@app.route('/api/courses', methods=['GET'])
def get_courses():
    return jsonify(courses)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)