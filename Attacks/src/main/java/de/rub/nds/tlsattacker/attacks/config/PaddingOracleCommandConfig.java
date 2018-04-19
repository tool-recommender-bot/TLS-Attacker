/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.attacks.config;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import de.rub.nds.tlsattacker.attacks.constants.PaddingRecordGeneratorType;
import de.rub.nds.tlsattacker.attacks.constants.PaddingVectorGeneratorType;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.config.delegate.CiphersuiteDelegate;
import de.rub.nds.tlsattacker.core.config.delegate.ClientDelegate;
import de.rub.nds.tlsattacker.core.config.delegate.GeneralDelegate;
import de.rub.nds.tlsattacker.core.config.delegate.HostnameExtensionDelegate;
import de.rub.nds.tlsattacker.core.config.delegate.ProtocolVersionDelegate;
import de.rub.nds.tlsattacker.core.constants.AlgorithmResolver;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.exceptions.ConfigurationException;
import java.util.LinkedList;
import java.util.List;

public class PaddingOracleCommandConfig extends AttackConfig {

    public static final String ATTACK_COMMAND = "padding_oracle";

    @Parameter(names = "-recordEngine", description = "The record generator used for the PaddingOracle")
    private PaddingRecordGeneratorType recordGeneratorType = PaddingRecordGeneratorType.SHORT;

    @Parameter(names = "-vectorEngine", description = "The vector generator used for the PaddingOracle")
    private PaddingVectorGeneratorType vectorGeneratorType = PaddingVectorGeneratorType.CLASSIC;

    @ParametersDelegate
    private ClientDelegate clientDelegate;
    @ParametersDelegate
    private HostnameExtensionDelegate hostnameExtensionDelegate;
    @ParametersDelegate
    private CiphersuiteDelegate ciphersuiteDelegate;
    @ParametersDelegate
    private ProtocolVersionDelegate protocolVersionDelegate;

    public PaddingOracleCommandConfig(GeneralDelegate delegate) {
        super(delegate);
        clientDelegate = new ClientDelegate();
        hostnameExtensionDelegate = new HostnameExtensionDelegate();
        ciphersuiteDelegate = new CiphersuiteDelegate();
        protocolVersionDelegate = new ProtocolVersionDelegate();
        addDelegate(clientDelegate);
        addDelegate(hostnameExtensionDelegate);
        addDelegate(ciphersuiteDelegate);
        addDelegate(protocolVersionDelegate);
    }

    public PaddingRecordGeneratorType getRecordGeneratorType() {
        return recordGeneratorType;
    }

    public void setRecordGeneratorType(PaddingRecordGeneratorType recordGeneratorType) {
        this.recordGeneratorType = recordGeneratorType;
    }

    public PaddingVectorGeneratorType getVectorGeneratorType() {
        return vectorGeneratorType;
    }

    public void setVectorGeneratorType(PaddingVectorGeneratorType vectorGeneratorType) {
        this.vectorGeneratorType = vectorGeneratorType;
    }

    @Override
    public boolean isExecuteAttack() {
        return false;
    }

    @Override
    public Config createConfig() {
        Config config = super.createConfig();
        if (ciphersuiteDelegate.getCipherSuites() == null) {
            List<CipherSuite> cipherSuites = new LinkedList<>();
            for (CipherSuite suite : CipherSuite.getImplemented()) {
                if (suite.isCBC() && !suite.isPsk() && !suite.isSrp()) {
                    cipherSuites.add(suite);
                }
            }
            config.setDefaultClientSupportedCiphersuites(cipherSuites);
        }
        for (CipherSuite suite : config.getDefaultClientSupportedCiphersuites()) {
            if (!suite.isCBC()) {
                throw new ConfigurationException("This attack only works with CBC Ciphersuites");
            }
        }
        config.setQuickReceive(true);
        // config.setEarlyStop(true);
        config.setAddRenegotiationInfoExtension(true);
        config.setAddServerNameIndicationExtension(true);
        config.setAddSignatureAndHashAlgrorithmsExtension(true);
        config.setQuickReceive(true);
        config.setStopActionsAfterFatal(true);
        config.setStopRecievingAfterFatal(true);
        config.setEarlyStop(true);
        boolean containsEc = false;
        for (CipherSuite suite : config.getDefaultClientSupportedCiphersuites()) {
            if (AlgorithmResolver.getKeyExchangeAlgorithm(suite).name().toUpperCase().contains("EC")) {
                containsEc = true;
            }
        }
        config.setAddECPointFormatExtension(containsEc);
        config.setAddEllipticCurveExtension(containsEc);

        return config;
    }
}
