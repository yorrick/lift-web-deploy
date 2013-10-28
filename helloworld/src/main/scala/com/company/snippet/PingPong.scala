package com.company
package snippet

import scala.actors._
import scala.actors.Actor._

object PingPong extends App {

  var count = 0;

  val pong = actor {
    loop {
      react {
        case Ping => println("Actor Pong Received Ping")
          sender ! Pong
        case Stop => println("Stopping Pong")
          exit()
      }
    }
  }

  val ping = actor {
    pong ! Pong

    loop {

      react {
        case Pong => println("Actor Ping Received Pong")
          count = count + 1;
          if (count < 3) {
            sender ! Ping
          } else {
            sender ! Stop
            println("Stopping Ping")
            exit()
          }
      }

    }
  }

}


case object Ping
case object Pong
case object Stop