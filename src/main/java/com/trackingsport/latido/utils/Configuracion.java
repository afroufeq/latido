/*
 * Configuracion.java
 * Proyecto:  Monitor de servicios
 * Copyright: 2015, TrackingSport
 */
package com.trackingsport.latido.utils;

import java.io.*;
import java.util.ArrayList;
import org.ini4j.Ini;

/**
 *
 * @author afroufeq
 */
public class Configuracion {
  private static ArrayList<String> servicios = new ArrayList<>();
  private static ArrayList<String> emails = new ArrayList<>();

  public static boolean readConfig() {
    Ini ini = new Ini();
    try {
//      String configFileName = System.getProperty( "user.dir" ) + "/" + Constantes.FICHERO_CONFIGURACION;
      String configFileName = Constantes.PATH_CONFIGURACION + "/" + Constantes.FICHERO_CONFIGURACION;
      // Comprobamos si existe el fichero de configuración en el mismo directorio en que estamos lanzando el cliente
      File configFile = new File( configFileName );
      if( !configFile.exists() ) {
        // SI no existe, error y salimos
        System.out.println( "FATAL: no se encuentra el fichero de configuracion " + Constantes.FICHERO_CONFIGURACION );
        System.exit( 1 );
      }
      ini.load( new FileReader( configFile ) );
      // ------------------------------------------------------ Sección Cliente
      for( String key: ini.get( Constantes.SECCION_1_SERVICIO ).keySet() ) {
        System.out.println( key + " -> " + ini.get( Constantes.SECCION_1_SERVICIO ).fetch( key ) );
        servicios.add( ini.get( Constantes.SECCION_1_SERVICIO ).fetch( key ) );
      }
      for( String key: ini.get( Constantes.SECCION_2_EMAIL ).keySet() ) {
        System.out.println( key + " -> " + ini.get( Constantes.SECCION_2_EMAIL ).fetch( key ) );
        emails.add( ini.get( Constantes.SECCION_2_EMAIL ).fetch( key ) );
      }
      System.out.println( "Cargado el fichero de configuración." );
    }catch( IOException e ) {
      System.out.println( "FATAL: Fallo procesando fichero configuración: " + e.getMessage() );
      return false;
    }
    return true;
  }

  public static ArrayList<String> getServicios() {
    return servicios;
  }

  public static ArrayList<String> getEmails() {
    return emails;
  }

}
