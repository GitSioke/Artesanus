from flask import Flask, request, jsonify, g
from flask_restful import Resource, Api
from sqlalchemy import create_engine, func
from flask_sqlalchemy import SQLAlchemy
from json import dumps

def create_heat(db):
    # This is the ORM for a Heat table.
    class Heat(db.Model):
        id = db.Column(db.Integer, primary_key=True)
        id_brew = db.Column(db.Integer, db.ForeignKey('brew.id'))
        position = db.Column(db.Integer)
        temperature = db.Column(db.Integer)
        duration = db.Column(db.Integer)
        startTime = db.Column(db.Integer)

        # Initialization function.
        def __init__(self, *args, **kwargs):
            if kwargs is not None:
                for key, value in kwargs.iteritems():
                    setattr(self, key, value)

        # This function is used to provide a json of this table.
        def serializable(self):
	    return { 'id':self.id, 'id_brew':self.id_brew, 'position':self.position, 'temperature':self.temperature , 'duration':self.duration, 'startTime':self.startTime }
	
    return Heat

