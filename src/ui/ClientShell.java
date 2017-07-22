package ui; /**
 * Created by Robbe on 7/14/2017.
 */

import asg.cliche.Shell;
import logic_client.Client;
import logic_client.User;

public class ClientShell {

    protected Shell theSHell;
    protected User user;

    ClientShell(User user){
        this.user = user;
    }



    public void cliSetShell(Shell theShell) {
        this.theSHell = theShell;

    }
}

