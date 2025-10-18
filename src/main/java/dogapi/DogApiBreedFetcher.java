package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://dog.ceo/api/breed/";
    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String url = API_URL + breed + "/list";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute())  {
            if (response.body() == null) {
                throw new BreedNotFoundException("Empty response from server");
            }

            String body = response.body().string();
            JSONObject json = new JSONObject(body);

            if (json.getString("status").equalsIgnoreCase("error")) {
                throw new BreedNotFoundException(json.getString("message"));
            }

            JSONArray messageArray = json.getJSONArray("message");
            List<String> subBreeds = new ArrayList<>();
            for (int i = 0; i < messageArray.length(); i++) {
                subBreeds.add(messageArray.getString(i));
            }

            return subBreeds;

        } catch (IOException e) {
            throw new BreedNotFoundException("Failed to fetch breed info: " + e.getMessage());
        }
    }
}
