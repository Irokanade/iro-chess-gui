package com.iro.engine;

import com.iro.board.Board;
import com.iro.board.Moves;
import com.iro.board.PieceEnum;
import com.iro.board.SquareEnum;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class UciClient {
    private final String enginePath;
    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;

    public UciClient() {
        String os = getOperatingSystem();
        String projectRoot = getProjectRoot();
        this.enginePath = projectRoot + "/engine/" + os + "/iro-chess" + getExecutableExtension();
    }

    public void start() throws IOException {
        System.out.println("Running engine from " + enginePath);

        File engineFile = new File(enginePath);
        if (!engineFile.exists()) {
            throw new IOException("Engine binary not found at: " + enginePath);
        }

        if (!engineFile.canExecute()) {
            engineFile.setExecutable(true);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(enginePath);
        this.process = processBuilder.start();
        this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    }

    public void sendCommand(String command) throws IOException {
        writer.write(command);
        writer.newLine();
        writer.flush();
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }

    public String readBestMove() throws IOException {
        String bestMove;
        do {
            bestMove = reader.readLine();
        } while(bestMove != null && !bestMove.startsWith("bestmove"));

        return bestMove;
    }

    public String getBestMove(Moves historyMoveList) {
        StringBuilder positionCommand = new StringBuilder("position startpos");

        if (historyMoveList.count > 0) {
            positionCommand.append(" moves");

            for (int i = 0; i < historyMoveList.count; i++) {
                int move = historyMoveList.moves[i];

                SquareEnum source = Moves.getMoveSource(move);
                SquareEnum target = Moves.getMoveTarget(move);

                positionCommand.append(" ");
                positionCommand.append(source.toString().toLowerCase());
                positionCommand.append(target.toString().toLowerCase());

                PieceEnum promoted = Moves.getMovePromoted(move);
                if (promoted.ordinal()!= 0) {
                    char promotionChar = promoted.name().toLowerCase().charAt(0);
                    if (promotionChar != ' ') {
                        positionCommand.append(promotionChar);
                    }
                }
            }
        }

        try {
            sendCommand(positionCommand.toString());
            sendCommand("go depth 6");

            return readBestMove();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void close() {
        try {
            if (process != null && process.isAlive()) {
                process.destroy();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String getProjectRoot() {
        try {
            String jarPath = UciClient.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(jarPath);

            if (jarFile.isFile()) {
                return jarFile.getParent();
            }
            else {
                String classPath = jarFile.getAbsolutePath();
                if (classPath.contains("build/classes")) {
                    return new File(classPath).getParentFile()
                        .getParentFile().getParentFile().getParentFile().getAbsolutePath();
                }
                return System.getProperty("user.dir");
            }
        } catch (Exception e) {
            return System.getProperty("user.dir");
        }
    }

    private String getOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) return "mac";
        if (os.contains("win")) return "windows";
        return "linux";
    }

    private String getExecutableExtension() {
        return System.getProperty("os.name").toLowerCase().contains("win") ? ".exe" : "";
    }
}
