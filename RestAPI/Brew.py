from flask import Flask, request, jsonify, g
from flask_restful import Resource, Api
from sqlalchemy import create_engine, func
from flask_sqlalchemy import SQLAlchemy
from json import dumps
import Hop
import Cereal
import Heat

# This is the ORM for a Brew table

def create_brew(db):
    class Brew(db.Model):
        __tablename__ = 'brew'
    
        id = db.Column(db.Integer, primary_key=True)
        startDate = db.Column(db.String(20))
        endDate = db.Column(db.String(20))
        litres = db.Column(db.Integer)
        beerType = db.Column(db.String(30))
        name = db.Column(db.String(50))
        cereals = db.relationship('Cereal', backref = 'brew_cereal')
        heats = db.relationship('Heat', backref = 'brew_heat')
        hops = db.relationship('Hop', backref = 'brew_hop')    

        # Initialization function
        def __init__(self, *args, **kwargs):
            if kwargs is not None:
                for key, value in kwargs.iteritems():
                    setattr(self, key, value)
                
        # This function is used to provide a json of this table
        def serializable(self, cereals, heats, processes, events):
            heaters = [heat.serializable() for heat in heats]
            cer = [cereal.serializable() for cereal in cereals]
            hps = [hop.serializable() for hop in hops]
            prcs = [process.serializable() for process in processes]
            ret = { 'id': self.id, 'startDate': self.startDate, 'endDate': self.endDate, 'name': self.name, 'litres': self.litres, 'beerType': self.beerType, 'cereals': cer, 'heats': heaters, 'hops':hps, 'processes':prcs }
            return ret

	# This function is used to provide a json of this table
        def serializable2(self):
            ret = { 'id': self.id, 'startDate': self.startDate, 'endDate': self.endDate, 'name': self.name, 'litres': self.litres, 'beerType': self.beerType }
            return ret

    return Brew
