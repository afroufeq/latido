/*
 * SesEmail.java
 * Proyecto:  Monitor de servicios
 * Copyright: 2015, TrackingSport
 */
package com.trackingsport.latido;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.metrics.AwsSdkMetrics;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.trackingsport.latido.utils.Configuracion;
import com.trackingsport.latido.utils.Constantes;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author afroufeq
 */
public class SesEmail {
  private final Logger log = Logger.getLogger( this.getClass() );

  public static SesEmail getInstance() {
    return Singleton.instancia;
  }

  public SendEmailResult enviarMailingClient( List<String> emails,String mensaje,String asunto ) {
    // Fijamos las credenciales de acceso al API de AmazonAWS
    log.debug( "SesEmail.enviarMailingClient | Awskey [" + Constantes.AWS_KEY + "]" );
    BasicAWSCredentials credenciales = new BasicAWSCredentials( Constantes.AWS_KEY,Constantes.AWS_SECRET );
    Region regionAws = Region.getRegion( Regions.EU_WEST_1 );
    // Creamos la petición de email pasando directamente quién es el origen
    SendEmailRequest sesrequest = new SendEmailRequest();
    String from = "Monitor Servicios " + Constantes.NO_REPLY_EMAIL;
    sesrequest.setSource( from );
    // Montamos la dirección de envío
    // Se permiten hasta 50 direcciones como máximo en la lista
    // Se mandan en BCC, para que solamente vea cada receptor del correo su propia dirección y no la de los
    // 50 que reciben la tanda de correo
    Destination dest = new Destination().withBccAddresses( emails );
    sesrequest.setDestination( dest );
    try {
      // Asunto
      Content subjContent = new Content().withData( asunto );
      // Creamos el mensaje. No es un mensaje JavaMail sino que es propio de AmazonAWS.
      // Permite enviar una versión texto y una versión html a la vez
      com.amazonaws.services.simpleemail.model.Message msg
        = new com.amazonaws.services.simpleemail.model.Message().withSubject( subjContent );
      // Fijamos el cuerpo del texto, en base al fichero de email que hayamos diseñado
      Content htmlContent = new Content().withData( mensaje );
      Body body = new Body().withHtml( htmlContent );
//    Body body = new Body().withHtml( htmlContent ).withText( textContent );
      msg.setBody( body );
      sesrequest.setMessage( msg );
      // Inicializamos el cliente SES con las credenciales
      AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient( credenciales );
      client.setRegion( regionAws );
      AwsSdkMetrics.disableMetrics();
      // Llamamos a Amazon SES y enviamos el mensaje
      return ( client.sendEmail( sesrequest ) );
    }catch( AmazonClientException e ) {
      log.warn( "SesEmail.enviarMailingClient | Amazon SES, EX-" + e.getMessage() );
    }
    return null;
  }

  public void sendFalloConfiguracion() {
    String asunto = "ERROR configuración Latido";
    String mensaje = "ERROR:\n" + Configuracion.getError();
    SendEmailResult res = enviarMailingClient( Configuracion.getEmails(),mensaje,asunto );
    log.info( "SesMail.sendFalloConfiguracion | Amazon SES, res-" + res.getMessageId() );
  }

  public void sendErrores( ArrayList<String> errores ) {
    String asunto = "ERROR Latido acceso Servicios ["+Configuracion.getFecha()+"]";
    StringBuilder sb = new StringBuilder();
    sb.append( "Error en el acceso a los Servicios TrackingSport:<br><br>" );
    for( String error: errores ) {
      sb.append( error ).append( "<br>" );
    }
    SendEmailResult res = enviarMailingClient( Configuracion.getEmails(),sb.toString(),asunto );
   log.info( "SesEmail.sendErrores | Amazon SES, res-" + res.getMessageId() );
  }

  private static final class Singleton {
    public static final SesEmail instancia = new SesEmail();
  }
}
