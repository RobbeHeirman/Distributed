package ui;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import avro.distributed.proto.ClientDetails;
import logic_client.User;
import java.io.IOException;
import java.util.List;


public class ShellClosed extends ClientShell {

    public ShellClosed(User user){
        super(user);
    }

    @Command
    public CharSequence getDevicesUsers(){

        List<ClientDetails> det = user.getDevicesUsers();
        CharSequence response = "";

        for (ClientDetails item : det){
            CharSequence type = "";

            switch (item.getType()){

                case USER:
                    type = "User";
                    break;
                case FRIDGE:
                    type = "Fridge";
                    break;
                case LIGHT:
                    type = "Light";
                    break;
                case SENSOR:
                    type = "Sensor";
                    break;
            }
            CharSequence online;
            if(item.getStatus()){
                online = "Online";
            }
            else {
                online = "Offline";
            }
            response = response.toString() + item.getName() + "\t" + type + "\t" + online + "\n";
        }

        return response;
    }

    @Command
    public CharSequence getLightsStatus(){

        return user.getLightsStatus();
    }

    @Command
    public CharSequence switch_light(CharSequence light){

        if (user.switch_light(light)){
            return "Device: " + light.toString() + " switched!";
        }

        else{
            return "Did not switch light " + light + ". Does it exist/is it online?";
        }
    }

    @Command
    public CharSequence getFridgeInventory(CharSequence fridge){
        return user.getFridgeInventory(fridge);

    }

    @Command
    public CharSequence openFridge(CharSequence fridge){

        ShellOpen open = new ShellOpen(user);
        if (user.open_fridge(fridge)){
            try {
                ShellFactory.createConsoleShell(fridge + " Open", "OpenFridge", open).commandLoop();
                user.close_fridge();
            } catch (IOException e) {
                System.err.println("Problems opening the ConsoleShell");
            }
            return "";
        }
        return fridge + " is not a valid fridge.";
    }

    @Command
    public CharSequence get_current_temprature(){

        float temperature = user.current_temperature();
        return "Device: The current temperature is: " + temperature;
    }

    @Command
    public CharSequence get_temperature_history(){

        String ret = "<Old>\n";

        List<Float> temperature_list = user.get_temperature_history();
        int count = 1;
        for(float temperature: temperature_list){
            ret = String.format("%s%d: %s\n", ret, count, temperature);
            count++;
        }
        ret = ret + "<new>";
        return ret;
    }

}
