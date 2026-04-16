package com.project.hotel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MvcpersonasApplication {

    private static final Logger log = LoggerFactory.getLogger(MvcpersonasApplication.class);

    private static final String ENV_TNS_ADMIN = "TNS_ADMIN";
    private static final String SYS_TNS_ADMIN = "oracle.net.tns_admin";

    public static void main(String[] args) {
        configureOracleWallet();
        SpringApplication.run(MvcpersonasApplication.class, args);
    }

    private static void configureOracleWallet() {
        String envTnsAdmin = System.getenv(ENV_TNS_ADMIN);
        if (envTnsAdmin != null && !envTnsAdmin.isBlank()) {
            Path envPath = Paths.get(envTnsAdmin).toAbsolutePath().normalize();
            validateWalletDirectoryOrThrow(envPath, "TNS_ADMIN env var");
            System.setProperty(SYS_TNS_ADMIN, envPath.toString());
            log.info("Oracle wallet configured from {}: {}", ENV_TNS_ADMIN, envPath);
            return;
        }

        List<Path> candidates = new ArrayList<>();
        Path workingDir = Paths.get("").toAbsolutePath().normalize();
        candidates.add(workingDir.resolve("wallet").resolve("Wallet_DondeJuanaDB"));
        candidates.add(workingDir.resolve("wallet"));

        List<String> checked = new ArrayList<>();
        for (Path candidate : candidates) {
            Path normalized = candidate.toAbsolutePath().normalize();
            checked.add(normalized.toString());
            if (isValidWalletDirectory(normalized)) {
                System.setProperty(SYS_TNS_ADMIN, normalized.toString());
                log.info("Oracle wallet configured automatically from project path: {}", normalized);
                return;
            }
        }

        String message = "No Oracle wallet found. Checked paths: " + checked
                + ". Ensure wallet files exist in wallet/Wallet_DondeJuanaDB (or set TNS_ADMIN). Required files: tnsnames.ora, sqlnet.ora.";
        log.error(message);
        throw new IllegalStateException(message);
    }

    private static void validateWalletDirectoryOrThrow(Path walletDir, String source) {
        if (!isValidWalletDirectory(walletDir)) {
            String message = "Invalid Oracle wallet directory from " + source + ": " + walletDir
                    + ". Required files: tnsnames.ora, sqlnet.ora.";
            log.error(message);
            throw new IllegalStateException(message);
        }
    }

    private static boolean isValidWalletDirectory(Path walletDir) {
        return Files.isDirectory(walletDir)
                && Files.isRegularFile(walletDir.resolve("tnsnames.ora"))
                && Files.isRegularFile(walletDir.resolve("sqlnet.ora"));
    }
}