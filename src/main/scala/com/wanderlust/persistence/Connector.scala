package com.wanderlust.persistence

import com.datastax.driver.core.Session
import com.datastax.driver.core.Cluster

// CREATE TABLE story_detail (place text, user_id uuid, date timestamp, id uuid, title text, sections frozen<list<text>>,
// category_list frozen<list<text>>, accompany_list frozen<list<text>>, external_links frozen<list<text>>, PRIMARY KEY ((place, user_id), date));
// story_brief should also include thumbnail image
// CREATE TABLE story_brief (place text, userId uuid, date timestamp, title text, id uuid, PRIMARY KEY (userId, date));
sealed trait Connector[T] {
  def getSession: T
  def close
}

//class CassandraConnector extends Connector {
trait CassandraConnector extends Connector[Session] {
  private lazy val cluster = Cluster.builder.addContactPoint("localhost").withPort(9142).build
  private lazy val session = cluster.connect // put inside try - handle with shutting 
  // 12 factor - treat dependent services and platforms ervices like db services equaly as a resource 
  // 
    
  override def getSession = session  
  override def close = {
    session.close
    cluster.close
  }
}

// Learnings: Abstraction makes sense in relational DB sense where queries adhere to SQL standard
// In NoSql world, this abstraction might not make so much sense because every no-sql modelling and 
// underlying priniciples are very different from each other to cater to specific needs 
// like GraphQL(graph), Mongo(document based), Dynamo(key value), etc.
//object MongoConnector extends ConnectorService {
//  private val mongoClient: MongoClient = MongoClient("mongodb://localhost")
//  private val connector: MongoDatabase = mongoClient.getDatabase("mydb")
//  def getConnector = connector
//  def close() = {
//    mongoClient.close()
//  }
//}