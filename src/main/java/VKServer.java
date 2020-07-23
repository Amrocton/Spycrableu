import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import data.Forecast;
import data.GeoCoords;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class VKServer {
    public static VKCore vkCore;
    static {
        try {
            vkCore = new VKCore();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running server...");
        while (true) {
            Thread.sleep(300);
            try {
                Message msg = vkCore.getMessage();
                if (msg != null && (!msg.getText().isEmpty()))
                    Executors.newCachedThreadPool().execute(()-> {
                        try {
                            sendMessage(getReplyMessage(msg),/*User id*/msg.getPeerId(),msg.getRandomId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            } catch (ClientException e) {
                System.out.println("Повторное соединение..");
                Thread.sleep(10000);
            }
        }
    }

    public static String getReplyMessage(Message msg) throws IOException {
        String userMessage = msg.getText();
        int userId = msg.getPeerId();
        GeoCoords city_coords = new GeoCoords(userMessage);
        Forecast weather = new Forecast(city_coords.getCoords());
        weather.Weather();
        JSONObject forecast = new JSONObject(weather.response);
        int temp = forecast.getJSONObject("fact").getInt("temp");
        int feels_like = forecast.getJSONObject("fact").getInt("feels_like");
        String condition = forecast.getJSONObject("fact").getString("condition");
        int pressure_mm = forecast.getJSONObject("fact").getInt("pressure_mm");
        String replyMessage = String.format("Weather Forecast: Moscow\n" +
                "Temperature: %d ℃\n" +
                "Feels like: %d ℃\n" +
                "Conditional: %s\n" +
                "Pressure: %d\n" +
                "Full info: https://yandex.ru/pogoda/moscow", temp, feels_like, condition, pressure_mm);
        return replyMessage;
    }

    public static void sendMessage(String message, int userId, int randomId) {
        try { vkCore.vk.messages().send(vkCore.actor).userId(userId).randomId(randomId).message(message).execute(); }
        catch (ApiException | ClientException e){ e.printStackTrace(); }
    }
}
