package com.madhukaraphatak.akka.local

import java.io.{File, FileInputStream}
import java.util.Properties

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Local actor which listens on any free port
  */
class LocalActor extends Actor {
  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    /*
      Connect to remote actor. The following are the different parts of actor path

      akka.tcp : enabled-transports  of remote_application.conf

      RemoteSystem : name of the actor system used to create remote actor

      127.0.0.1:5150 : host and port

      user : The actor is user defined

      remote : name of the actor, passed as parameter to system.actorOf call

     */
    val propMessage = new Properties
    propMessage.load(new FileInputStream("AkkaDummy.properties"))
    val remoteActor = context.actorSelection(propMessage.getProperty("remote.uri"))
    println("That 's remote:" + remoteActor)
    remoteActor ! propMessage.getProperty("remote.inputs")
  }

  override def receive: Receive = {

    case msg: String => {
      println("got message from remote" + msg)
    }
  }
}


object LocalActor {

  def main(args: Array[String]) {
    val config = ConfigFactory.parseFile(new File("local_application.conf"))
    println("Config: " + config.getConfig("akka"))
    val system = ActorSystem("ClientSystem", config)
    val localActor = system.actorOf(Props[LocalActor], name = "local")
  }


}
