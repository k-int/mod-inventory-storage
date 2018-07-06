package org.folio.rest.support;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.net.URL;

public class HttpClient {
  private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

  private static final String TENANT_HEADER = "X-Okapi-Tenant";

  private final io.vertx.core.http.HttpClient client;

  public HttpClient(Vertx vertx) {
    client = vertx.createHttpClient();
  }

  public void post(
    URL url,
    Object body,
    String tenantId,
    Handler<HttpClientResponse> responseHandler) {

    if(body == null) {
      postText(url, null, tenantId, responseHandler);
    }
    else {
      postText(url, Json.encodePrettily(body), tenantId, responseHandler);
    }
  }

  public void postText(
    URL url,
    String encodedBody,
    String tenantId,
    Handler<HttpClientResponse> responseHandler) {

    HttpClientRequest request = client.postAbs(url.toString(), responseHandler);

    request.headers().add("Accept","application/json, text/plain");
    request.headers().add("Content-type","application/json");

    if (tenantId != null) {
      request.headers().add(TENANT_HEADER, tenantId);
    }

    if (encodedBody == null) {
      request.end();
      return;
    }

    log.debug("POST {0}, Request: {1}", url.toString(), encodedBody);
    request.end(encodedBody);
  }

  public void put(
    URL url,
    Object body,
    String tenantId,
    Handler<HttpClientResponse> responseHandler) {

    HttpClientRequest request = client.putAbs(url.toString(), responseHandler);

    request.headers().add("Accept","application/json, text/plain");
    request.headers().add("Content-type","application/json");

    if(tenantId != null) {
      request.headers().add(TENANT_HEADER, tenantId);
    }

    request.end(Json.encodePrettily(body));
  }

  public void get(
    URL url,
    String tenantId,
    Handler<HttpClientResponse> responseHandler) {

    get(url.toString(), tenantId, responseHandler);
  }

  public void get(
    String url,
    String tenantId,
    Handler<HttpClientResponse> responseHandler) {

    HttpClientRequest request = client.getAbs(url, responseHandler);

    request.headers().add("Accept","application/json");

    if(tenantId != null) {
      request.headers().add(TENANT_HEADER, tenantId);
    }

    request.end();
  }

  public void delete(
    URL url,
    String tenantId,
    Handler<HttpClientResponse> responseHandler) {

    delete(url.toString(), tenantId, responseHandler);
  }

  public void delete(
    String url,
    String tenantId,
    Handler<HttpClientResponse> responseHandler) {

    HttpClientRequest request = client.deleteAbs(url, responseHandler);

    request.headers().add("Accept","application/json, text/plain");

    if(tenantId != null) {
      request.headers().add(TENANT_HEADER, tenantId);
    }

    request.end();
  }
}
