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
import com.trackingsport.latido.utils.Constantes;
import java.util.List;

/**
 *
 * @author afroufeq
 */
public class SesEmail {

  public SendEmailResult enviarMailingClient( List<String> emails,String evento,String mensaje,String asunto ) {
    // Fijamos las credenciales de acceso al API de AmazonAWS
    System.out.println( "PostAwsSES.enviarMailingClient | Awskey [" + Constantes.AWS_KEY + "]" );
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
      System.out.println( "PostAwsSES.enviarMailingClient | Amazon SES, EX-" + e.getMessage() );
    }
    return null;
  }

}
