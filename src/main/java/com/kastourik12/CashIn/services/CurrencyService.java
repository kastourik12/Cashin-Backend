package com.kastourik12.CashIn.services;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class CurrencyService {

    public String ChangeCurrency(String to,String from, String amount) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.apilayer.com/fixer/convert?to="+to+"&from="+from+"&amount="+amount)
                .addHeader("apikey", "Ry3pWUE7SYC5L49a4v4inzLBF4pB4drp")
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            return  response.body().string().split("\n")[12].split(":")[1];
        } catch (IOException e) {
            return null;
        }

            }


}
