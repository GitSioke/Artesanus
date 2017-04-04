from flask import Flask, request, jsonify, g
from flask_restful import Resource, Api
from sqlalchemy import create_engine, func
from flask_sqlalchemy import SQLAlchemy
from json import dumps

# This method would return an entity process to operate with Process table.
def create_process(db):
    # This is the ORM for a Process table.
    class Process(db.Model):
        id = db.Column(db.Integer, primary_key=True)
        id_brew = db.Column(db.Integer, db.ForeignKey('brew.id'))
        type = db.Column(db.String(30))
        startTime = db.Column(db.BigInteger)
        endTime = db.Column(db.BigInteger)
        events = db.relationship('Event', backref = 'process_event')
        

        # Initialization function.
        def __init__(self, *args, **kwargs):
            if kwargs is not None:
                for key, value in kwargs.iteritems():
                    setattr(self, key, value)

        # This function is used to provide a json of this table.
        def serializable(self, events):
            evs = [event.serializable() for event in events]
	    return {'id':self.id,'id_brew':self.id_brew,'type':self.type,'startTime':self.startTime,'endTime':self.endTime,'events':evs}
	

	# This function is used to provide a json of this table.
        def serializable2(self):
	    return { 'id':self.id, 'id_brew':self.id_brew, 'type':self.type, 'startTime':self.startTime , 'endTime':self.endTime }
	

    return Process

