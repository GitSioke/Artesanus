from flask import Flask, request, jsonify, g
from flask_restful import Resource, Api
from sqlalchemy import create_engine, func, and_
from flask_sqlalchemy import SQLAlchemy
from json import dumps
from Brew import create_brew
from Hop import create_hop
from Heat import create_heat
from Cereal import create_cereal
from Event import create_event
from Process import create_process
from datetime import datetime

# Create a engine for connecting to SQLite3.
# Assuming salaries.db is in your app root folder

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///artesanus.sqlite'
api = Api(app)

db = SQLAlchemy(app)

brew_entity = create_brew(db)
cereal_entity = create_cereal(db)
hop_entity = create_hop(db)
heat_entity = create_heat(db)
process_entity = create_process(db)
event_entity = create_event(db)
db.create_all()

# This function returns the next id based on current number of rows.
def next_id(query):
	count_query = query.statement.with_only_columns([func.count()]).order_by(None)
        count = query.session.execute(count_query).scalar()
        return count+1



@app.route('/create', methods = ['GET'])
def index():
    #SQLiteBrew.create_brew_table(e)
    return jsonify({'brews': Brew.query.all()})    



# Brew region

@app.route('/create_brew')
def create_brew():
    br = brew_entity("hola","yo")
    db.session.add(br)
    db.session.commit()
    return jsonify({'result' : True})

@app.route('/insert_brew', methods = ['POST'])
def insert_brew():
    query_br = db.session.query(brew_entity)
    id_br = next_id(query_br)
    query_pr = db.session.query(process_entity)
    id_pr = next_id(query_pr)
    print(id_pr)
    data = request.get_json()

    #Get processes section
    processes = data.get('processes')
    for process in processes:
        # Get events section and creat objects by iteration
	events = process.get('events')
        for event in events:
            ev = event_entity(id_process=id_pr , type=event.get('type'), source=event.get('source'), message=event.get('message'), value=event.get('value'), data=event.get('data'), time=event.get('time') )
            db.session.add(ev)
        # Creat process object by iteration
        pr = process_entity(id_brew=id_br, type=process.get('type'), startTime=process.get('startTime'), endTime=process.get('endTime'))
        db.session.add(pr)

    # Get cereals section and create cereal objects by iterarion.
    cereals=data.get('cereals')
    for cereal in cereals:
	cr = cereal_entity(id_brew=id_br, name=cereal.get('name'), quantity=cereal.get('quantity'))
        db.session.add(cr)

    # Get hops section and create hop objects by iteration.
    hops=data.get('hops')
    for hop in hops:
        hp = hop_entity(id_brew=id_br, name=hop.get('name'), quantity=hop.get('quantity'), minutes=hop.get('minutes'))
        db.session.add(hp)

    # Get heats section and create heat objects by iteration.
    heats = data.get('heats')
    for heat in heats:
        ht = heat_entity(id_brew=id_br, position=heat.get('position'), duration=heat.get('duration'), temperature=heat.get('temperature'), startTime=heat.get('startTime'))
        db.session.add(ht)

    # Create a brew object with retrieved data.
    br = brew_entity(name=data.get('name'), beerType=data.get('beerType'), litres=data.get('litres'), startDate=data.get('startDate'))
    db.session.add(br)
    
    # Commit all the additions and return a json with True
    db.session.commit()
    return jsonify({'result' : True})

@app.route('/update_brew/<int:id>', methods = ['POST'])
def update_brew(id):
    print(request)
    print(request.headers)
    data = request.get_json()
    print(data)
    brew = brew_entity.query.get(id)
    brew.startDate = data.get('startDate')
    print(data.get('startDate'))
    brew.endDate = data.get('endDate')
    db.session.commit()
    print jsonify({'brew': brew.serializable2()})
    return jsonify({'brew': brew.serializable2()})



# Events region

# retrieve all events related to id_process given as parameter
@app.route('/retrieve/events/<int:id_process>', methods= ['GET'])
def get_events(id_process):
    query = (db.session.query(process_entity).join(event_entity).filter(process_entity.id == id_process))
    for process in query:
        print(process)
        process_serialized = process.serializable(process.events)
    return jsonify(process_serialized)

@app.route('/insert/events/', methods= ['POST'])
def insert_event():
    data_json = request.get_json()
    date = datetime.now()
    print(date.strftime('%Y-%m-%d %H:%M:%S'))
    event = event_entity(id_process=data_json.get('id_process'), type=data_json.get('type'), data=data_json.get('data'), value=data_json.get('value'), time=date.strftime('%Y-%m-%d %H:%M:%S'))
    db.session.add(event)
    db.session.commit()
    return jsonify({'result' : True})

@app.route('/retrieve/last_mashing_event/', methods= ['POST'])
def retrieve_last_mashing_event():
    id_process = "none" 
    event = (db.session.query(event_entity).filter(and_(event_entity.source == "MASHING", event_entity.type == "COMMAND", event_entity.message == "START")).order_by(event_entity.id.desc()).first())
    print(event)
    id_process = event.id_process
    print(id_process)
    return jsonify({'result' : id_process})




@app.route('/test')
def test():
    q = (db.session.query(brew_entity).join(cereal_entity)).all()
    for brew in q:
        brew_serialized = brew.serializable(brew.cereal, brew.heat)
        
    return jsonify({'brew': brew_serialized})


    
# Ingredients region

@app.route('/insert_ingredient')
def insert_ingredient():
    ig = Ingredient(1, "papaya", 2 , 10)
    db.session.add(ig)
    db.session.commit()
    return jsonify({'result' : True})



# Heats region

@app.route('/insert_heat')
def insert_heat():
    heat = Heat(1, 1, 10000, 10000)
    db.session.add(heat)
    db.session.commit()
    return jsonify({'result' : True})



if __name__ == '__main__':
    # run api in host 0.0.0.0 to have external access
    app.run(host='0.0.0.0')
    app.run()
