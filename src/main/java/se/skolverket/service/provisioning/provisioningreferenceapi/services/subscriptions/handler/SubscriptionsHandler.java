package se.skolverket.service.provisioning.provisioningreferenceapi.services.subscriptions.handler;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import se.skolverket.service.provisioning.provisioningreferenceapi.services.subscriptions.SubscriptionsService;
import se.skolverket.service.provisioning.provisioningreferenceapi.services.subscriptions.model.Subscription;

import java.util.UUID;

import static se.skolverket.service.provisioning.provisioningreferenceapi.common.helper.Constants.*;
import static se.skolverket.service.provisioning.provisioningreferenceapi.common.helper.RequestHelper.response201Json;
import static se.skolverket.service.provisioning.provisioningreferenceapi.common.helper.RequestHelper.response204;

@Slf4j
public class SubscriptionsHandler {

  private SubscriptionsHandler() {}

  public static Handler<RoutingContext> postSubscriptions(SubscriptionsService subscriptionsService) {
    return routingContext -> {
      log.info("Temp using log, SubscriptionsHandler routingContext: {}", routingContext);
      JsonObject requestBody = routingContext.body().asJsonObject();
      requestBody.put(ID, UUID.randomUUID().toString());
      Subscription postedSub = new Subscription(requestBody);
      log.info("Temp using log, SubscriptionsHandler postedsub: {}", postedSub);
      subscriptionsService.createSubscription(postedSub)
        .onSuccess((Subscription subscription) -> response201Json(
          routingContext, buildSubscriptionJsonObject(subscription)
        ))
        .onFailure(throwable -> {
          log.info("Temp using log, SubscriptionsHandler throwable: {}", throwable);
          routingContext.fail(throwable);
        });
    };
  }

  private static JsonObject buildSubscriptionJsonObject(Subscription subscription) {
    // JSON representation should look a little different outwards. "resourceTypes"
    // needs to be an array of objects rather than an array of Strings.
    JsonObject jsonObject = subscription.toJson();
    JsonArray resourceTypeArray = new JsonArray();
    subscription.getResourceTypes().forEach(
      rt -> resourceTypeArray.add(new JsonObject().put(RESOURCE, rt.toString()))
    );
    jsonObject.put(RESOURCE_TYPES, resourceTypeArray);
    return jsonObject;
  }

  public static Handler<RoutingContext> deleteSubscriptions(SubscriptionsService subscriptionsService) {
    return routingContext -> subscriptionsService.deleteSubscription(routingContext.pathParam(PP_SUBSCRIPTION_ID))
      .onSuccess(v -> response204(routingContext))
      .onFailure(routingContext::fail);
  }
}
