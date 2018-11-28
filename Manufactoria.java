import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Manufactoria {

    private static char inputType;

    /**
     * @param args the path to the file we want to interpret
     */
    public static void main(String[] args) {
        char[][] program = parse(validate(args));
        char[] input = getInput(args);
        int x = -1, y = -1;
        out:
        for (int findy = 0; findy < program.length; findy++) {
            for (int findx = 0; findx < program[findy].length; findx++) {
                if (program[findy][findx] == '@' || program[findy][findx] == '0' || program[findy][findx] == '&') {
                    x = findx + 1;
                    y = findy;
                    break out;
                }
            }
        }
        if (x == -1 || y == -1) {
            exit("Internal Error: starting point not found after starting point "
                    + "was validated");
        }
        ArrayDeque<Character> queue = new ArrayDeque<>();
        for (char c : input) {
            queue.add(c);
        }
        int lastdir = 0b00;
        char[] dirs = {'l', 'd', 'u', 'r'};

        while (true) {
            char p = ' ';
            char dir = 'n';
            char peek;
            int matchl = 0, matchr = 0;
            try {
                p = program[y][x];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.exit(1); //for robot moving off board
            }
            switch (p) {
                case ' ':
                case '@':
                case '&':
                case '0':
                    System.exit(1);
                case '$':
                    int count = 0;
                    char toPrint = 0;
                    for (char c : queue) {
                        if (count == 7) {
                            System.out.print(toPrint);
                            count = 0;
                            toPrint = 0;
                        }
                        char val = queue.pop();
                        if (val != 'g' && val != 'y') {
                            toPrint <<= 1;
                            if (val == 'b') {
                                toPrint |= 1;
                            }
                            count++;
                        }
                    }
                    if (toPrint != 0) {
                        System.out.print(toPrint);
                    }
                    System.out.println();
                    System.exit(0);
                case ';':
                    queue.stream().forEach((c) -> {
                        System.out.print(c);
                    });
                    System.out.println();
                    System.exit(0);
                case '!':
                    BigInteger ans = BigInteger.ZERO;
                    queue.stream().forEach((c) -> {
                        if (c != 'g' && c != 'y') {
                            if (c == 'b') {
                                ans.or(BigInteger.ONE);
                            }
                            ans.shiftLeft(1);
                        }
                    });
                    ans.shiftRight(1);
                    System.out.println(ans);
                case '.':
                    System.exit(0);
                case '>':
                    dir = 'r';
                    break;
                case '<':
                    dir = 'l';
                    break;
                case '^':
                    dir = 'u';
                    break;
                case 'v':
                    dir = 'd';
                    break;
                case '#':
                    switch (lastdir) {
                        case 0b00:
                            x++;
                            break;
                        case 0b10:
                            x--;
                            break;
                        case 0b01:
                            y--;
                            break;
                        case 0b11:
                            y++;
                            break;
                        default:
                            exit("Internal error: faulty bridge code");
                    }
                    break;
                case 'r':
                    queue.add('r');
                    dir = 'r';
                    break;
                case 'c':
                    queue.add('r');
                    dir = 'u';
                    break;
                case 'R':
                    queue.add('r');
                    dir = 'l';
                    break;
                case 'C':
                    queue.add('r');
                    dir = 'd';
                    break;
                case 'b':
                    queue.add('b');
                    dir = 'r';
                    break;
                case 'd':
                    queue.add('b');
                    dir = 'u';
                    break;
                case 'B':
                    queue.add('b');
                    dir = 'l';
                    break;
                case 'D':
                    queue.add('b');
                    dir = 'd';
                    break;
                case 'g':
                    queue.add('g');
                    dir = 'r';
                    break;
                case 'q':
                    queue.add('g');
                    dir = 'u';
                    break;
                case 'G':
                    queue.add('g');
                    dir = 'l';
                    break;
                case 'Q':
                    queue.add('g');
                    dir = 'd';
                    break;
                case 'y':
                    queue.add('y');
                    dir = 'r';
                    break;
                case 't':
                    queue.add('y');
                    dir = 'u';
                    break;
                case 'Y':
                    queue.add('y');
                    dir = 'l';
                    break;
                case 'T':
                    queue.add('y');
                    dir = 'd';
                    break;
                case 'U':
                    matchl = 'y' - 'g';
                    matchr = 'g' - 'y';
                case 'u':
                    matchl += 'g' - 'r';
                    matchr += 'y' - 'b';
                case 'H':
                    matchl += 'r' - 'b';
                    matchr += 'b' - 'r';
                case 'h':
                    matchl += 'b';
                    matchr += 'r';
                    dir = dirs[branch(0, (char) queue.peek(), (char) matchl, (char) matchr)];
                    peek = (char) queue.peek();
                    if (peek == matchl || peek == matchr) {
                        queue.pop();
                    }
                    break;
                case 'I':
                    matchl = 'y' - 'g';
                    matchr = 'g' - 'y';
                case 'i':
                    matchl += 'g' - 'r';
                    matchr += 'y' - 'b';
                case 'J':
                    matchl += 'r' - 'b';
                    matchr += 'b' - 'r';
                case 'j':
                    matchl += 'b';
                    matchr += 'r';
                    dir = dirs[branch(1, (char) queue.peek(), (char) matchl, (char) matchr)];
                    peek = (char) queue.peek();
                    if (peek == matchl || peek == matchr) {
                        queue.pop();
                    }
                    break;
                case 'O':
                    matchl = 'y' - 'g';
                    matchr = 'g' - 'y';
                case 'o':
                    matchl += 'g' - 'r';
                    matchr += 'y' - 'b';
                case 'K':
                    matchl += 'r' - 'b';
                    matchr += 'b' - 'r';
                case 'k':
                    matchl += 'b';
                    matchr += 'r';
                    dir = dirs[branch(2, (char) queue.peek(), (char) matchl, (char) matchr)];
                    peek = (char) queue.peek();
                    if (peek == matchl || peek == matchr) {
                        queue.pop();
                    }
                    break;
                case 'P':
                    matchl = 'y' - 'g';
                    matchr = 'g' - 'y';
                case 'p':
                    matchl += 'g' - 'r';
                    matchr += 'y' - 'b';
                case 'L':
                    matchl += 'r' - 'b';
                    matchr += 'b' - 'r';
                case 'l':
                    matchl += 'b';
                    matchr += 'r';
                    dir = dirs[branch(1, (char) queue.peek(), (char) matchl, (char) matchr)];
                    peek = (char) queue.peek();
                    if (peek == matchl || peek == matchr) {
                        queue.pop();
                    }
                    break;
                default:
                    exit("Invalid char in source code: " + p);
            }
            switch (dir) {
                case 'r':
                    x++;
                    lastdir = 0b00;
                    break;
                case 'u':
                    y--;
                    lastdir = 0b01;
                    break;
                case 'l':
                    x--;
                    lastdir = 0b10;
                    break;
                case 'd':
                    y++;
                    lastdir = 0b11;
                    break;
            }
        }
    }

    private final static int[] mapbranch = {1, 3, 0, 2};

    public static int branch(int branchaccess, char queueremove,
            char matchl, char matchr) {
        return queueremove == matchr ? mapbranch[branchaccess]
                : queueremove == matchl ? mapbranch[3 - branchaccess]
                : branchaccess;
    }

    public static void exit(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    public static File validate(String[] args) {
        if (args.length < 1) {
            exit("Must provide a source file");
        }
        String filepath = args[0];
        File source = new File(filepath);
        if (!source.exists()) {
            exit("Source file must exist");
        }
        if (!source.isFile()) {
            exit("File specified is not a valid file");
        }
        int index = filepath.lastIndexOf('.');
        if (!filepath.substring(index + 1).equalsIgnoreCase("mfa")) {
            exit("File is not of the correct type.\n"
                    + "Make sure the file is of type mfa.");
        }
        return source;
    }

    public static char[][] parse(File source) {
        char[][] toreturn = null;
        boolean inputCharFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(source))) {
            List<String> lines = new ArrayList<>();
            for (String line; (line = reader.readLine()) != null;) {
                if (line.contains("@")) {
                    if (inputCharFound) {
                        exit("Cannot have multiple starting points in program");
                    }
                    inputCharFound = true;
                    inputType = '@';
                }
                if (line.contains("0")) {
                    if (inputCharFound) {
                        exit("Cannot have multiple starting points in program");
                    }
                    inputCharFound = true;
                    inputType = '0';
                }
                if (line.contains("&")) {
                    if (inputCharFound) {
                        exit("Cannot have multiple starting points in program");
                    }
                    inputCharFound = true;
                    inputType = '&';
                }
                lines.add(line);
            }
            if (!inputCharFound) {
                exit("No found starting point of program");
            }
            reader.close();
            toreturn = new char[lines.size()][];
            for (int index = 0; index < lines.size(); index++) {
                toreturn[index] = lines.get(index).toCharArray();
            }
        } catch (FileNotFoundException ex) {
            exit("Internal error in method parse: file was validated but not found.");
        } catch (IOException ex) {
            exit("Internal error in method parse");
        }
        return toreturn;
    }

    public static char[] getInput(String[] args) {
        String in = args[args.length - 1]; //let's just get the last one
        if (inputType == '@') {
            if (in.matches(".*[bryg]*.*")) {
                return in.toCharArray();
            }
            exit("Cannot take invalid input.");
            return null;
        } else if (inputType == '0') {
            BigInteger b = new BigInteger(in);
            return b.toString(2).replace("1", "b").replace("0", "r").toCharArray();
        } else {
            char[] toRet = new char[in.length() * 7];
            for (int i = 0; i < in.length(); i++) {
                char c = in.charAt(i);
                if (c > 128) {
                    exit("This interpreter does not support unicode");
                }
                String s = Integer.toBinaryString((int) c);
                while (s.length() < 7) {
                    s = "0" + s;
                }
                if (s.length() > 7) {
                    exit("Internal error: in inputting ascii values");
                }
                System.arraycopy(s.toCharArray(), 0, toRet, i * 7, 7);
            }
            return toRet;
        }
    }

}
