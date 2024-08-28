package snt.rmrt.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import snt.rmrt.models.readyReckoner.networkElement.NEInput;
import snt.rmrt.models.readyReckoner.networkElement.NetworkElement;

import java.io.IOException;
import java.util.*;

public class NetworkElementDeserializer extends StdDeserializer<NetworkElementDeserializer> {

    public NetworkElementDeserializer() {
        this(null);
    }

    public NetworkElementDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public NetworkElement deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {

        NetworkElement networkElement = new NetworkElement();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Double release = node.get("Release").asDouble();
        String networkElementType = node.get("Network Element Type").asText().trim();
        String domain = node.get("Domain").asText().trim();

        networkElement.setNetworkElementType(networkElementType);
        networkElement.setRelease(release);
        networkElement.setDomain(domain);

        //get the inputs if any
        if (node.has("Input")) {
            Set<NEInput> inputsFields = new HashSet<>();

            JsonNode inputJsonArray = node.get("Input");

            getInputs(inputJsonArray, inputsFields);

            //one to many
            networkElement.setInputs(inputsFields);

        }


        //check for outputs
        if(node.has("Output")) {
            Map<String, String> output = new HashMap<>();

            JsonNode outputJsonArray = node.get("Output");

            getJsonArrayContent(outputJsonArray, output);

            networkElement.setOutputs(output);
        }



        if(node.has("InputPrePopulated")) {
            Map<String, String> prePopulatedInput = new HashMap<>();

            JsonNode prePopulatedJsonArr = node.get("InputPrePopulated");

            getJsonArrayContent(prePopulatedJsonArr, prePopulatedInput);

            networkElement.setInputPrePopulated(prePopulatedInput);
        }



        return networkElement;
    }





    public void getInputs(JsonNode jsonArray, Set<NEInput> extractedFields) {
        for(int i = 0; i < jsonArray.size(); ++i ) {

            JsonNode jsonObject = jsonArray.get(i);

            Iterator<Map.Entry<String, JsonNode>> objectNodes = jsonObject.fields();

            int j = 1;

            while(objectNodes.hasNext()) {
                //get the key value pairs
                Map.Entry<String, JsonNode> entry = objectNodes.next();

                extractedFields.add(new NEInput(entry.getKey(), entry.getValue().asText(), j));

                j++;
            }
        }
    }





    public void getJsonArrayContent(JsonNode jsonArray, Map<String, String> extractedFields) {
        for(int i = 0; i < jsonArray.size(); ++i ) {

            JsonNode jsonObject = jsonArray.get(i);

            Iterator<Map.Entry<String, JsonNode>> objectNodes = jsonObject.fields();

            while(objectNodes.hasNext()) {
                //get the key value pairs
                Map.Entry<String, JsonNode> entry = objectNodes.next();

                extractedFields.put(entry.getKey(), entry.getValue().asText());
            }
        }
    }



}
