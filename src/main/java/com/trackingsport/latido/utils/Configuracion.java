/*
 * Configuracion.java
 * Proyecto:  Monitor de servicios
 * Copyright: 2015, TrackingSport
 */
package com.trackingsport.latido.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import org.ini4j.Ini;

/**
 *
 * @author afroufeq
 */
public class Configuracion {
  private final static Logger log = Logger.getLogger( Configuracion.class.getName() );
  private static final ArrayList<String> servicios = new ArrayList<>();
  private static final ArrayList<String> emails = new ArrayList<>();
  private static String error;

  public static boolean readConfig() {
    boolean ret = true;
    error = null;
    Ini ini = new Ini();
    try {
      String configFileName = Constantes.PATH_CONFIGURACION + "/" + Constantes.FICHERO_CONFIGURACION;
      // Comprobamos si existe el fichero de configuración en el mismo directorio en que estamos lanzando el cliente
      File configFile = new File( configFileName );
      if( !configFile.exists() ) {
        ret = false;
        error = "Fichero no existe: " + Constantes.FICHERO_CONFIGURACION;
        // SI no existe, error y salimos
        log.warning( "Configuracion | FATAL: no se encuentra el fichero de configuracion " + Constantes.FICHERO_CONFIGURACION );
      }
      ini.load( new FileReader( configFile ) );
      // ------------------------------------------------------ Sección Cliente
      servicios.clear();
      for( String key: ini.get( Constantes.SECCION_1_SERVICIO ).keySet() ) {
        log.fine( key + " -> " + ini.get( Constantes.SECCION_1_SERVICIO ).fetch( key ) );
        servicios.add( key + "#" + ini.get( Constantes.SECCION_1_SERVICIO ).fetch( key ) );
      }
      emails.clear();
      for( String key: ini.get( Constantes.SECCION_2_EMAIL ).keySet() ) {
        log.fine( key + " -> " + ini.get( Constantes.SECCION_2_EMAIL ).fetch( key ) );
        emails.add( ini.get( Constantes.SECCION_2_EMAIL ).fetch( key ) );
      }
      log.fine( "Configuracion | Cargado el fichero de configuración." );
    }catch( IOException e ) {
      ret = false;
      error = "EX-Procesando configuración: " + e.getMessage();
      log.warning( "Configuracion | FATAL: Fallo procesando fichero configuración: " + e.getMessage() );
    }
    return ret;
  }

  public static String codeStr( int httpCode ) {
    StringBuilder sb = new StringBuilder();
    sb.append( "[" ).append( httpCode ).append( "] " );
    switch( httpCode ) {
      case 100:
        sb.append( "Continue" );
        break;
      case 101:
        sb.append( "Switching Protocols" );
        break;
      case 102:
        sb.append( "Processing" );
        break;
      case 200:
        sb.append( "OK" );
        break;
      case 201:
        sb.append( "Created" );
        break;
      case 202:
        sb.append( "Accepted" );
        break;
      case 203:
        sb.append( "Non-Authoritative Information" );
        break;
      case 204:
        sb.append( "No Content" );
        break;
      case 205:
        sb.append( "Reset Content" );
        break;
      case 206:
        sb.append( "Partial Content" );
        break;
      case 207:
        sb.append( "Multi-Status" );
        break;
      case 208:
        sb.append( "Already Reported" );
        break;
      case 226:
        sb.append( "IM Used" );
        break;
      case 300:
        sb.append( "Multiple Choices" );
        break;
      case 301:
        sb.append( "Moved Permanently" );
        break;
      case 302:
        sb.append( "Found" );
        break;
      case 303:
        sb.append( "See Other" );
        break;
      case 304:
        sb.append( "Not Modified" );
        break;
      case 305:
        sb.append( "Use Proxy" );
        break;
      case 306:
        sb.append( "Switch Proxy" );
        break;
      case 307:
        sb.append( "Temporary Redirect" );
        break;
      case 308:
        sb.append( "Permanent Redirect" );
        break;
      case 400:
        sb.append( "Bad Request" );
        break;
      case 401:
        sb.append( "Unauthorized" );
        break;
      case 402:
        sb.append( "Payment Required" );
        break;
      case 403:
        sb.append( "Forbidden" );
        break;
      case 404:
        sb.append( "Not Found" );
        break;
      case 405:
        sb.append( "Method Not Allowed" );
        break;
      case 406:
        sb.append( "Not Acceptable" );
        break;
      case 407:
        sb.append( "Proxy Authentication Required" );
        break;
      case 408:
        sb.append( "Request Timeout" );
        break;
      case 409:
        sb.append( "Conflict" );
        break;
      case 410:
        sb.append( "Gone" );
        break;
      case 411:
        sb.append( "Length Required" );
        break;
      case 412:
        sb.append( "ContiPrecondition Failednue" );
        break;
      case 413:
        sb.append( "Payload Too Large" );
        break;
      case 414:
        sb.append( "ConRequest-URI Too Longtinue" );
        break;
      case 415:
        sb.append( "ContinUnsupported Media Typeue" );
        break;
      case 416:
        sb.append( "Requested Range Not Satisfiable" );
        break;
      case 417:
        sb.append( "Expectation Failed" );
        break;
      case 418:
        sb.append( "I'm a teapot" );
        break;
      case 419:
        sb.append( "Authentication Timeout" );
        break;
      case 420:
        sb.append( "Method Failure" );
        break;
      case 421:
        sb.append( "Misdirected Request" );
        break;
      case 422:
        sb.append( "Unprocessable Entity" );
        break;
      case 423:
        sb.append( "Locked" );
        break;
      case 424:
        sb.append( "Failed Dependency" );
        break;
      case 426:
        sb.append( "ContiUpgrade Requirednue" );
        break;
      case 428:
        sb.append( "Precondition Required" );
        break;
      case 429:
        sb.append( "Too Many Requests" );
        break;
      case 431:
        sb.append( "Request Header Fields Too Large" );
        break;
      case 440:
        sb.append( "Login Timeout" );
        break;
      case 444:
        sb.append( "No Response" );
        break;
      case 449:
        sb.append( "Retry With" );
        break;
      case 450:
        sb.append( "Blocked by Windows Parental Controls" );
        break;
      case 451:
        sb.append( "Redirect" );
        break;
      case 494:
        sb.append( "Request Header Too Large" );
        break;
      case 495:
        sb.append( "Cert Error" );
        break;
      case 496:
        sb.append( "No Cert" );
        break;
      case 497:
        sb.append( "HTTP to HTTPS" );
        break;
      case 498:
        sb.append( "Token expired/invalid" );
        break;
      case 499:
        sb.append( "Client Closed Request" );
        break;
      case 500:
        sb.append( "Internal Server Error" );
        break;
      case 501:
        sb.append( "Not Implemented" );
        break;
      case 502:
        sb.append( "Bad Gateway" );
        break;
      case 503:
        sb.append( "Service Unavailable" );
        break;
      case 504:
        sb.append( "Gateway Timeout" );
        break;
      case 505:
        sb.append( "HTTP Version Not Supported" );
        break;
      case 506:
        sb.append( "Variant Also Negotiates" );
        break;
      case 507:
        sb.append( "Insufficient Storage" );
        break;
      case 508:
        sb.append( "Loop Detected" );
        break;
      case 509:
        sb.append( "Bandwidth Limit Exceeded" );
        break;
      case 510:
        sb.append( "Not Extended" );
        break;
      case 511:
        sb.append( "Network Authentication Required" );
        break;
      case 520:
        sb.append( "Unknown Error" );
        break;
      case 522:
        sb.append( "Origin Connection Time-out" );
        break;
      case 598:
        sb.append( "Network read timeout error" );
        break;
      case 599:
        sb.append( "Network connect timeout error" );
        break;
    }
    return sb.toString();
  }

  public static String getFecha() {
    String salida;
    Calendar cal = Calendar.getInstance();
    cal.setTime( new Date() );
    salida = String.format( "%02d/%02d/%4d %02d:%02d:%02d",
      cal.get( Calendar.DAY_OF_MONTH ),cal.get( Calendar.MONTH ) + 1,cal.get( Calendar.YEAR ),
      cal.get( Calendar.HOUR_OF_DAY ),cal.get( Calendar.MINUTE ),cal.get( Calendar.SECOND ) );
    return salida;
  }

  public static String getError() {
    return error;
  }

  public static ArrayList<String> getServicios() {
    return servicios;
  }

  public static ArrayList<String> getEmails() {
    return emails;
  }

}
