/*
Name: Joshua Thaisananikone and Abdul Wahab Malik
Group: 17
 */
public class Main {
    public static void main(String[] args) {
        // INDICES of all arrays will corresponds with the position of its corresponding instruction in
        // in instructions array.
        // We will use this to figure out the instructions(first array) being called and used it to
        // get op code(second array).
        String[][] instructions = {{"add", "addiu", "and", "andi", "beq", "bne", "j", "lui", "lw", "or", "ori",
                "slt", "sub", "sw", "syscall"},
                {"000000", "001001", "000000", "001100", "000100", "000101",
                        "000010", "001111", "100011", "000000", "001101", "000000",
                        "000000", "101011", "000000"}}; // Each array has a length of 15.
        //First array is for shamt. Second array is for function
        String[][] shamt_function = {{"00000", "", "00000", "", "", "", "", "", "", "00000", "", "00000", "00000", "", ""},//shamt
                {"100000", "", "100100", "", "", "", "", "", "", "100101", "", "101010", "100010", "", "001100"}};//functions
        String[] special_rs = {"00000"};//contains rs for lui (respectively)

        //Array full of registers with the index being used as register index for binary conversion!
        String[]registers = {"$zero", "$at", "$v0", "$v1", "$a0", "$a1", "$a2", "$a3", "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7",
                "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7", "$t8", "$t9", "$k0", "$k1", "$gp", "$sp", "$fp", "$ra"};//binary version of register index.

        String command = args[0];
        String mnemonic = "";
        int Space1 = 0; // helps us find the end index for reading mnemonics.
        for (int i = 0; i < command.length(); i++){ //make sure that mnemonic is read regardless of number of spaces and tabs.
            char c = command.charAt(i);
            if (!Character.isWhitespace(c)) {
                Space1 = command.indexOf(" ", i);
                if (Space1 == -1){
                    mnemonic = command.substring(i, command.length());//should get the Mnemonics.
                }else{
                    mnemonic = command.substring(i, Space1);//should get the Mnemonics.
                }
                break;
            }
        }
        //Data field to convert instruction to binary.
        String opcode = "";
        String rs = "";
        String rt = "";
        String rd = "";
        String shamt = "";
        String function = "";


        int rs_index = 0;
        int rt_index = 0;
        int rd_index = 0;

        StringBuilder pre_result = new StringBuilder();
        StringBuilder result = new StringBuilder();
        long hex;

        //substring are second index exclusive.
        for (int i = 0; i < 15; i++) { //retrieve opcode, shamt, and function
            if (instructions[0][i].equals(mnemonic)) {
                opcode = instructions[1][i];
                shamt = shamt_function[0][i];
                function = shamt_function[1][i];
                break;
            }
        }

        //NOW WE NEED TO RETRIEVE THE ARGUMENTS FOR THE INSTRUCTIONS (Registers or immeadiate values)
        int first_comma_posit = command.indexOf(",", 0);//get first comma
        int second_comma_posit = 0;
        if (first_comma_posit != -1) { // -1 means no comma.
            second_comma_posit = command.indexOf(",", first_comma_posit + 1);//get second comma.
        }

        //retrieve the three registers parameters for appropriate mnemonics.
        //Prepare for conversion to binary.
        int space2 = 0; //enable us to read the register arguments properly.
        int register1 = 0;
        int register2 = 0;
        int register3 = 0;
        String immeadiate = "";
        String first_input = "";//Stores the first input if not a register
        String second_input = "";//Stores the second input if not a register
        int third_input = 0 ;//Store the second input if not a register.
        //HANDLING R-TYPES
        if(mnemonic.equals("add") || mnemonic.equals("and") || mnemonic.equals("or") || mnemonic.equals("slt")
                || mnemonic.equals("sub")) { // all of their arguments are registers or "$".
            register1 = command.indexOf("$", 0);
            rd = command.substring(register1, first_comma_posit);//retrieve rd

            register2 = command.indexOf("$", register1 + 1);
            rs = command.substring(register2, second_comma_posit);//retrieve rs

            register3 = command.indexOf("$", register2 + 1);
            rt = command.substring(register3, register3 + 3); //It will ignore comments

            //get the register index
            for (int i = 0; i < registers.length; i++) {
                if (rd.equals(registers[i])) {
                    rd_index = i;
                }
                if (rs.equals(registers[i])) {
                    rs_index = i;
                }
                if (rt.equals(registers[i])) {
                    rt_index = i;
                }
            }
            rd = giveBinary(rd_index,5);
            rs = giveBinary(rs_index,5);
            rt = giveBinary(rt_index,5);
            pre_result.append(opcode);
            if(rs.length() < 5){ //add any missing zeros.
                for(int i = 0; i < 5 - rs.length(); i++){
                    pre_result.append("0");
                }
            }
            pre_result.append(rs);
            if(rt.length() < 5){ //add any missing zeros.
                for(int i = 0; i < 5 - rt.length(); i++){
                    pre_result.append("0");
                }
            }
            pre_result.append(rt);
            if(rd.length() < 5){ //add any missing zeros.
                for(int i = 0; i < 5 - rd.length(); i++){
                    pre_result.append("0");
                }
            }
            pre_result.append(rd);
            pre_result.append(shamt);
            pre_result.append(function);
            long temp = 0;
            for(int i = 0; i < 32; i +=4){
                String binary;
                binary = pre_result.substring(i, i + 4);
                temp = Long.parseLong(binary, 2);
                result.append(decimalToHex(temp));
            }
            System.out.println(result.toString());

            //HANDLING I-TYPE
        } else if(mnemonic.equals("addiu") || mnemonic.equals("beq") || mnemonic.equals("bne") || mnemonic.equals("andi")
                || mnemonic.equals("ori")){ //addiu,beq,bne working.
            int comment_index = command.indexOf("#");
            register1 = command.indexOf("$", 0);
            rs = command.substring(register1, first_comma_posit);//retrieve rs

            register2 = command.indexOf("$", register1 + 1);
            rt = command.substring(register2, second_comma_posit);//retrieve rt

            third_input =  command.indexOf("0x");

            if (third_input == -1){ // if third input isn't a hexadecimal.
                if (comment_index == -1){//not comment at all
                    immeadiate = command.substring(second_comma_posit + 2, command.length()).trim();
                }else{//exist a comment.
                    immeadiate = command.substring(second_comma_posit + 2, comment_index).trim();
                }
            }else{ //if third input is a hexdecimal
                if (comment_index == -1){//no comments
                    immeadiate = command.substring(third_input + 2, command.length()).trim();
                }else {//exist a comment
                    immeadiate = command.substring(third_input + 2, comment_index).trim();
                }

            }

            rs_index = getRegister(rs, registers);
            rt_index = getRegister(rt, registers);
            rs = giveBinary(rs_index,5);
            rt = giveBinary(rt_index,5);
            pre_result.append(opcode);
            if(mnemonic.equals("beq") || mnemonic.equals("bne")){
                if(rs.length() < 5){ //add any missing zeros.
                    for(int i = 0; i < 5 - rs.length(); i++){
                        pre_result.append("0");
                    }
                }
                pre_result.append(rs);
                if(rt.length() < 5){
                    for(int i = 0; i < 5 - rt.length(); i++){
                        pre_result.append("0");
                    }
                }
                pre_result.append(rt);
            }else{
                if(rt.length() < 5){ //add any missing zeros.
                    for(int i = 0; i < 5 - rt.length(); i++){
                        pre_result.append("0");
                    }
                }
                pre_result.append(rt);
                if(rs.length() < 5){
                    for(int i = 0; i < 5 - rs.length(); i++){
                        pre_result.append("0");
                    }
                }
                pre_result.append(rs);
            }
            if(third_input == -1){ //if not hexadecimal
                immeadiate = giveBinary(Integer.parseInt(immeadiate),16);
                if(immeadiate.length() > 16){ //(mnemonic.equals("beq") || mnemonic.equals("bne") || mnemonic.equals("andi") || mnemonic.equals("ori")) &&
                    //chop off leading 1s to ensure binary is 32 bits.
                    immeadiate = immeadiate.substring(16,32);
                }
                for(int i = 0; i < 16 - immeadiate.length(); i++){
                    pre_result.append("0");
                }
                pre_result.append(immeadiate);
            }else{ //if hexadecimal
                for(int i = 0; i < 4 - immeadiate.length(); i++){
                    pre_result.append("0000");//add extra 0s.
                }
            }

            long temp = 0;
            for(int i = 0; i < pre_result.length(); i +=4){
                String binary;
                if(i + 4 > pre_result.length()){
                    binary = pre_result.substring(i, pre_result.length());
                }else{
                    binary = pre_result.substring(i, i + 4);
                }
                temp = Long.parseLong(binary, 2);
                result.append(decimalToHex(temp));
            }
            if (third_input != -1){
                result.append(immeadiate);
            }
            System.out.println(result.toString());//print out result
        } else if(mnemonic.equals("j")){
            int immead_index = command.indexOf("0x", 0);
            int comment_index = command.indexOf("#");
            if (comment_index == -1){//not comment at all
                immeadiate = command.substring(immead_index + 2, command.length()).trim();
            }else{//exist a comment.
                immeadiate = command.substring(immead_index + 2, comment_index).trim();
            }
            pre_result.append(opcode);
            for(int i = 0; i < 6 - immeadiate.length(); i++){
                pre_result.append("0000");//add extra 0s to ensure 32 bits.
            }

            //convert binary to hexadecimal
            long temp = 0;
            for(int i = 0; i < pre_result.length(); i +=4){
                String binary;
                if(i + 4 > pre_result.length()){
                    binary = pre_result.substring(i, pre_result.length());
                }else{
                    binary = pre_result.substring(i, i + 4);
                }
                temp = Long.parseLong(binary, 2);
                result.append(decimalToHex(temp));
            }
            result.append(immeadiate);

            System.out.println(result.toString());//print out result

        }else if(mnemonic.equals("lui")){
            rs = special_rs[0];
            register1 = command.indexOf("$");
            rt = command.substring(register1, first_comma_posit).trim();
            rt_index = getRegister(rt, registers);
            rt = giveBinary(rt_index,5);

            pre_result.append(opcode);
            int immead_index = command.indexOf("0x", 0);
            int comment_index = command.indexOf("#");
            if (comment_index == -1){//not comment at all
                immeadiate = command.substring(immead_index + 2, command.length()).trim();
            }else{//exist a comment.
                immeadiate = command.substring(immead_index + 2, comment_index).trim();
            }
            pre_result.append(rs);
            if(rt.length() < 5){ //add any missing zeros.
                for(int i = 0; i < 5 - rt.length(); i++){
                    pre_result.append("0");
                }
            }
            pre_result.append(rt);
            for(int i = 0; i < 4 - immeadiate.length(); i++){ // shift lefts by 16 bits.
                pre_result.append("0000");//add extra 0s to ensure 32 bits.
            }

            //convert binary to hexadecimal
            long temp = 0;
            for(int i = 0; i < pre_result.length(); i +=4){
                String binary;
                if(i + 4 > pre_result.length()){
                    binary = pre_result.substring(i, pre_result.length());
                }else{
                    binary = pre_result.substring(i, i + 4);
                }
                temp = Long.parseLong(binary, 2);
                result.append(decimalToHex(temp));
            }
            result.append(immeadiate);
            System.out.println(result.toString());
        } else if (mnemonic.equals("lw") || mnemonic.equals("sw")) { // opcode + base + rt + offset
            //String offset = "";
            int comment_index = command.indexOf("#");
            int parent1 = command.indexOf("(");
            int parent2 = command.indexOf(")");
            register1 = command.indexOf("$"); // gets rt
            register2 = command.indexOf("$", register1 + 1); // gets base (register inside of parentheses.

            rt = command.substring(register1, first_comma_posit).trim();
            rt_index = getRegister(rt, registers);//get register number for rt
            rs = command.substring(parent1 + 1, parent2);
            rs_index = getRegister(rs, registers); // get register number for base.
            rt = giveBinary(rt_index,5);
            rs = giveBinary(rs_index, 5);
            pre_result.append(opcode);
            if(rs.length() < 5){
                for(int i = 0; i < 5 - rs.length(); i++){
                    pre_result.append("0");
                }
            }
            pre_result.append(rs);

            if(rt.length() < 5){
                for(int i = 0; i < 5 - rt.length(); i++){
                    pre_result.append("0");
                }
            }
            pre_result.append(rt);

            String offset = command.substring(first_comma_posit + 1, parent1).trim();

            if(!offset.isBlank()){
                offset = giveBinary(Integer.parseInt(offset), 16);
                if(offset.length() < 16){ //shift offset if neccessary
                    for(int i = 0; i < 16 - offset.length(); i++){
                        pre_result.append("0");
                    }
                } else{
                    //chop off leading 1s to ensure binary is 32 bits.(Works with negative number)
                    offset = offset.substring(16,32);
                }
            }else{
                offset = "0000000000000000";//16 bits of offset if it doesn't exist.
            }

            pre_result.append(offset);

            //convert binary to hexadecimal
            long temp = 0;
            for(int i = 0; i < pre_result.length(); i +=4){
                String binary;
                if(i + 4 > pre_result.length()){
                    binary = pre_result.substring(i, pre_result.length());
                }else{
                    binary = pre_result.substring(i, i + 4);
                }
                temp = Long.parseLong(binary, 2);
                result.append(decimalToHex(temp));
            }
            System.out.println(result.toString());

        } else{ // will perform syscall for us.
            System.out.println("0000000c");
        }
    }

    public static int getRegister(String reg, String[] registers){
        int result = 0;
        for (int i = 0; i < registers.length; i++) {
            if (reg.equals(registers[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    public static String giveBinary(int n, int length) { // Convert from int to binary
        if (n < 0){ //uses default binary string method if integer to convert is negative.
            return Integer.toBinaryString(n);
        }
        StringBuilder result = new StringBuilder();
        // array to store binary number
        int[] binaryNum = new int[length];
        for (int i = 0; i < binaryNum.length; i++){
            binaryNum[i] = 0;
        }

        // counter for binary array
        int i = 0;
        while (n > 0) {
            // storing remainder in binary array
            binaryNum[i] = n % 2;
            n = n / 2;
            i++;
        }
        // printing binary array in reverse order
        for (int j = i - 1; j >= 0; j--) {
            result.append(Integer.toString(binaryNum[j]));
        }

        //return string version of binary.
        return result.toString();
    }


    // method to convert binary to decimal
    public static int binaryToDecimal(long binary) {

        // variable to store the converted
        // binary number
        int decimalNumber = 0, i = 1;

        while (binary > 0) {
            long lastDigit = binary % 10;  // Extract the last digit (0 or 1)
            decimalNumber += lastDigit * i;   // Add the value of the digit
            binary /= 10;  // Remove the last digit
            i *= 2;     // Move to the next base (2^1, 2^2, etc.)
        }

        // returning the decimal number
        return decimalNumber;
    }


    // method to convert decimal to hexadecimal
    public static String decimalToHex(long binary)
    {
        // converting the integer to the desired
        // hex string using toHexString() method
        String hexNumber
                = Long.toHexString(binary);

        // returning the final hex string
        return hexNumber;
    }

}

