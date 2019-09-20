package com.ardeapps.floorballmanager.services;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.utils.Logger;

import java.util.Arrays;
import java.util.List;

public class BillingService {

    private static BillingClient billingClient;
    List<SkuDetails> skuDetailsList;

    public static void initialize() {
        billingClient = BillingClient.newBuilder(AppRes.getContext()).enablePendingPurchases().setListener((billingResult, purchases) -> {
            // If user buys product, data will retrieve here
            Logger.toast("Purchases item: " + purchases.size());
        }).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Logger.toast("Success to connect Billing");
                } else {
                    Logger.toast(billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Logger.toast("You are disconnect Billing");
            }
        });
    }

    public void startBillingFlow() {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(0)).build();
        billingClient.launchBillingFlow(AppRes.getActivity(), billingFlowParams);
    }

    public void loadProduct(LoadProductListener listener) {
        if (billingClient.isReady()) {
            SkuDetailsParams products = SkuDetailsParams.newBuilder()
                    .setSkusList(Arrays.asList("analyze_chemistry"))
                    .setType(BillingClient.SkuType.INAPP)
                    .build();
            billingClient.querySkuDetailsAsync(products, (billingResult, skuDetailsList) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    BillingService.this.skuDetailsList = skuDetailsList;
                    Logger.toast(skuDetailsList);
                    Logger.log(skuDetailsList);
                    listener.onProductsLoaded(skuDetailsList);
                } else {
                    Logger.toast("Cannot query product");
                }
            });
        } else {
            Logger.toast("Billing client not ready");
        }
    }

    public interface LoadProductListener {
        void onProductsLoaded(List<SkuDetails> skuDetailsList);
    }

}
