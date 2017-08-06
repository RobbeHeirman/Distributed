package ui;

import asg.cliche.Command;
import logic_client.User;


public class ShellOpen extends ClientShell  {
    ShellOpen(User user) {
        super(user);
    }

    @Command
    public String addItem(CharSequence item) {

        user.add_item(item);
        return item + " is now in the fridge.";
    }

    @Command
    public String removeItem(CharSequence item){
        user.remove_item(item);
        return item + " is now removed.";
    }

    @Command
    public CharSequence fridgeInventory(){
        return user.getFridgeInventoryOpen();
    }
}
