package com.example.chatbot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

public static List<ChatModel> chatModelList = new ArrayList<>();
public static MassageAdapter massageAdapter;
private String username = "";
public static ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText messageInput = findViewById(R.id.send);
        ImageView sendButton = findViewById(R.id.sendbtn);
        RecyclerView recyclerView = findViewById(R.id.chatlist);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Retrieve the intent that started this activity
        Intent intent = getIntent();
        username = intent.getStringExtra("username"); // Use the same key
        progressBar.setVisibility(View.GONE);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = messageInput.getText().toString().trim();

                ChatModel chatModel = new ChatModel();
                chatModel.setId(1);
                chatModel.setUser(username);
                chatModel.setMassage(messageContent);
                chatModelList.add(chatModel);

                massageAdapter = new MassageAdapter(MainActivity.this,chatModelList);
                recyclerView.setAdapter(massageAdapter);
                massageAdapter.notifyDataSetChanged();

                if (!messageContent.isEmpty()) {
                    messageInput.setText("");
                    progressBar.setVisibility(View.VISIBLE);
                    new OpenAIExample().execute(messageContent);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

class OpenAIExample extends AsyncTask<String, Void, String> {

    private static final String TAG = "OpenAIExample";
    private TextView responseText;

    OpenAIExample() {

    }

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        try {
            URL url = new URL("https://api.llama-api.com/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer LL-N3I5SpXXuxMA6kjdh6ro3sBlQFnL3MPcGcCmauCI4nGCsnNH5W731VEC8nHZ91AY");
            connection.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            JSONArray messagesArray = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", params[0]);
            messagesArray.put(message);
            requestBody.put("messages", messagesArray);
            requestBody.put("functions", new JSONArray());
            requestBody.put("model", "codellama-7b-instruct");
            requestBody.put("stream", false);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line);
                }
                in.close();
                response = responseBuilder.toString();


            } else {
                Log.e(TAG, "HTTP error code: " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    String content = message.getString("content");

                    ChatModel chatModel = new ChatModel();
                    chatModel.setId(0);
                    chatModel.setUser("Llama");
                    chatModel.setMassage(content);
                    MainActivity.chatModelList.add(chatModel);
                    MainActivity.progressBar.setVisibility(View.GONE);
                    MainActivity.massageAdapter.notifyDataSetChanged();

                } else {
                    Log.e(TAG, "\"No answer received. ");
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception parsing JSON: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "\"No answer received. ");
        }
    }
}
