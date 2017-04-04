from flask import Flask, request, jsonify, g
from flask_restful import Resource, Api
from sqlalchemy import create_engine, func
from flask_sqlalchemy import SQLAlchemy
from json import dumps

def create_event(db):
    # This is the ORM for a Heat table.
    class Event(db.Model):
        id = db.Column(db.Integer, primary_key=True)
        id_process = db.Column(db.Integer, db.ForeignKey('process.id'))
	type = db.Column(db.String(30))
        source = db.Column(db.String(30))
	message = db.Column(db.String(150))
        value = db.Column(db.Integer)
        data = db.Column(db.String(30))
        time = db.Column(db.String(21))

        
        # Initialization function.
        def __init__(self, *args, **kwargs):
            if kwargs is not None:
                for key, value in kwargs.iteritems():
                    setattr(self, key, value)

        # This function is used to provide a json of this table.
        def serializable(self):
	    return {'id':self.id,'id_process':self.id_process,'source':self.source,'message':self.message,'value':self.value,'data':self.data,'time':self.time} 
	
    return Event

