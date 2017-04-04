from flask import Flask, request, jsonify, g
from flask_restful import Resource, Api
from sqlalchemy import create_engine, func
from flask_sqlalchemy import SQLAlchemy
from json import dumps

def create_cereal(db):
    # This is the ORM for a Cereal table.
    class Cereal(db.Model):
        id = db.Column(db.Integer, primary_key=True)
        id_brew = db.Column(db.Integer, db.ForeignKey('brew.id'))    
        name = db.Column(db.String(30))
        quantity = db.Column(db.Integer)
        time = db.Column(db.Integer)

        # Initialization function.
        def __init__(self, *args, **kwargs):
            if kwargs is not None:
                for key, value in kwargs.iteritems():
                    setattr(self, key, value)

        # This function is used to provide a json of this table.
        def serializable(self):
	    return { 'id':self.id, 'id_brew':self.id_brew, 'name':self.name, 'quantity':self.quantity, 'time':self.time }

    return Cereal