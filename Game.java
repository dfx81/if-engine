import java.util.Scanner;
import java.io.File;

class Game
{
    private int cur = 0;
    private String vrb = "";
    private String obj = "";
    
    private int[] inv;
    public Rooms[] r;
    public Items[] t;
    
    public static void main(String[] args)
    {
        Game g = new Game();
        Scanner in = new Scanner(System.in);
        
        while (g.cur != -1)
        {
            System.out.print("> ");
            String str = in.nextLine();
            
            g.vrb = g.obj = "";
            for (int i = 0; i != str.length(); i++)
            {
                if (str.charAt(i) != ' ') g.vrb += str.charAt(i);
                else
                {
                    for (i++; i != str.length(); i++)
                        g.obj += str.charAt(i);
                    break;
                }
            }
            
            g.check();
        }
    }
    
    void check()
    {
        if (vrb.equals("go"))
        {
            int now = cur;
            
            for (int i = 0; i != r[cur].exits.length; i++)
            {
                if (r[cur].exits[i].equals(obj))
                {
                    cur = r[cur].dests[i];
                    cout(r[cur].name);
                    cout(r[cur].desc);
                    
                    for (int j = 0; j != t.length; j++)
                    {
                        if (t[j].loc == cur && t[j].taken == 0)
                            cout(t[j].desc);
                    }
                    
                    if (r[cur].nExits == 0)
                        cur = r[cur].dests[0];
            
                    break;
                }
            }
            
            if (cur == now)
            {
                cout("I don't where that was...\n");
            }
        }
        
        else if (vrb.equals("exit"))
        {
            if (obj.equals("game"))
            {
                cout("Thanks for playing! ~ dfx\n");
                System.exit(0);
            }
        }
        
        else if (vrb.equals("look"))
        {
            if (obj.equals("inventory"))
            {
                if (inv[0] > 0)
                {
                    for (int i = 1; i != inv[0]; i++)
                    {
                        cout(t[inv[i]].qtty + " x " + t[inv[i]].name + "\n");
                    }
                }
                
                else
                {
                    cout("You have nothing on you...\n");
                }
            }
            
            else if (obj.length() > 0)
            {
                boolean found = false;
                
                for (int i = 1; i != inv[0]; i++)
                {
                    if (obj.equals(t[inv[i]].name))
                    {
                        cout(t[inv[i]].flav);
                        found = true;
                    }
                }
                
                if (!found)
                    cout("You don't have that...\n");
            }
            
            else if (obj.length() == 0)
                desc();
        }
        
        else if (vrb.equals("take"))
        {
            int tot = inv[0];
            
            for (int i = 0; i != t.length; i++)
            {
                if (obj.equals(t[i].name) && t[i].taken == 0)
                {
                    if (inv[0] != inv.length)
                    {
                        inv[inv[0]] = t[i].id;
                        t[i].taken = 1;
                        inv[0]++;
                        cout("OK\n");
                    }
                    
                    else
                    {
                        cout("You can't carry any more stuff...\n");
                    }
                }
            }
            
            if (tot == inv[0])
            {
                cout("There's no such item around...\n");
            }
        }
        
        else if (vrb.equals("use"))
        {
            boolean found = false;
            boolean wrong = false;
            
            for (int i = 1; i != inv[0]; i++)
            {
                if (obj.equals(t[inv[i]].name) && ((r[cur].id == t[inv[i]].usable) || t[inv[i]].usable == -1))
                {
                    if (t[inv[i]].type == 1 && t[inv[i]].qtty != 0)
                    {
                        cout(t[inv[i]].eftxt);
                        t[inv[i]].qtty--;
                        if (t[inv[i]].qtty == 0)
                        {
                            inv[i] = -1;
                            for (int j = i; j != inv[0] - 1; j++)
                            {
                                inv[j] = inv[j + 1];
                            }
                            
                            inv[0]--;
                        }
                    }
                    
                    else if (t[inv[i]].type == 2)
                    {
                        if (r[cur].danger != 0) cout(t[inv[i]].eftxt);
                        else cout("There's nothing to attack...\n");
                    }
                    
                    found = true;
                    break;
                }
                else if (obj.equals(t[inv[i]].name) && ((r[cur].id != t[inv[i]].usable) || t[inv[i]].usable != -1))
                {
                    wrong = true;
                    break;
                }
            }
            
            if (!found) cout("You don't have that...\n");
            else if (wrong) cout("You can't use that now...\n");
        }
        
        else
        {
            cout("I don't know what that means...\n");
        }
    }
    
    public Game()
    {
        setup();
        desc();
    }
    
    private void setup()
    {
        try
        {
            Scanner in = new Scanner(new File("Game.rooms"));
            cur = Integer.parseInt(in.next());
            r = new Rooms[Integer.parseInt(in.next())];
            for (int i = 0; i != r.length; i++)
            {
                r[i] = new Rooms();
                r[i].id = in.nextInt();
                in.nextLine();
                r[i].name = in.nextLine();
                r[i].desc = "";
                String str = "";
                while (!str.equals("//"))
                {
                    r[i].desc += str + "\n";
                    str = in.nextLine();
                }
                r[i].danger = in.nextInt();
                r[i].nExits = in.nextInt();
                if (r[i].nExits != 0)
                {
                    r[i].exits = new String[r[i].nExits];
                    for (int j = 0; j != r[i].nExits; j++)
                        r[i].exits[j] = in.next();
                }
                
                if (r[i].nExits != 0)
                {
                    r[i].dests = new int[r[i].nExits];
                    for (int j = 0; j != r[i].nExits; j++)
                        r[i].dests[j] = in.nextInt();
                }
                else
                {
                    r[i].dests = new int[1];
                    r[i].dests[0] = in.nextInt();
                }
                
            }
            
            in.close();
        }
        catch (Exception e)
        {
            System.out.println("ERROR READING ROOMS FILE");
            e.printStackTrace();
            System.exit(0);
        }
        
        try
        {
            Scanner in = new Scanner(new File("Game.items"));
            
            inv = new int[in.nextInt()];
            inv[0] = in.nextInt();
            
            t = new Items[in.nextInt()];
            
            for (int i = 0; i != t.length; i++)
            {
                t[i] = new Items();
                t[i].id = in.nextInt();
                t[i].type = in.nextInt();
                t[i].name = in.next();
                in.nextLine();
                t[i].desc = "";
                String str = in.nextLine();
                while (!str.equals("//"))
                {
                    t[i].desc += str + "\n";
                    str = in.nextLine();
                }
                t[i].flav = "";
                str = in.nextLine();
                while (!str.equals("//"))
                {
                    t[i].flav += str + "\n";
                    str = in.nextLine();
                }
                t[i].eftxt = "";
                str = in.nextLine();
                while (!str.equals("//"))
                {
                    t[i].eftxt += str + "\n";
                    str = in.nextLine();
                }
                t[i].loc = in.nextInt();
                t[i].usable = in.nextInt();
                t[i].taken = in.nextInt();
                t[i].qtty = in.nextInt();
            }
            
            in.close();
        }
        catch (Exception e)
        {
            System.out.println("ERROR READING ITEMS FILE");
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    void cout(String str)
    {
        for (int i = 0; i != str.length(); i++)
        {
            if (str.charAt(i) == '\\' && str.charAt(i + 1) == 'n')
            {
                System.out.println();
                i += 2;
            }
            
            System.out.print(str.charAt(i));
            
            try
            {
                Thread.sleep(5);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
    }
    
    private void desc()
    {
        cout(r[cur].name);
        cout(r[cur].desc);
        for (int i = 0; i != t.length; i++)
        {
            if (t[i].loc == cur && t[i].taken == 0)
                cout(t[i].desc);
        }
    }
}