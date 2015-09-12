/*
 * Constantes.java
 * Proyecto:  Monitor de servicios
 * Copyright: 2015, TrackingSport
 */
package com.trackingsport.latido.utils;

/**
 *
 * @author afroufeq
 */
public class Constantes {

  public static final String FICHERO_CONFIGURACION = "latido.ini";
//  public static final String PATH_CONFIGURACION = "C:/Desarrollo/netbeans/ZonaTrail/latido/src/main/resources";
//  public static final String PATH_CONFIGURACION = "/Users/afroufeq/desarrollo/netbeans/latido/src/main/resources";
  public static final String PATH_CONFIGURACION = "/home/ubuntu/.latido";
  public static final int INTERVALO_CHECK = 1800000;  // 30 minutos, en milisegundos 
  public static final int TIMEOUT = 30000;  // 30 segundos, en milisegundos 
  // fichero INI
  public static final int NUM_SECCIONES = 2;
  public static final String SECCION_1_SERVICIO = "servicios";
  public static final String SECCION_2_EMAIL = "email";

  // configuración de AmazonSES
  // region EU (Ireland)  - Regions.EU_WEST_1
  public static final String AWS_KEY = "AKIAJDFROTQLJ7QPGMOA";
  public static final String AWS_SECRET = "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";
  public static final String NO_REPLY_EMAIL = " <no-replyb@trackingsport.com>";

}
