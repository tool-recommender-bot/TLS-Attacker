/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.record.layer;

import de.rub.nds.tlsattacker.core.constants.ProtocolMessageType;
import de.rub.nds.tlsattacker.core.record.AbstractRecord;
import de.rub.nds.tlsattacker.core.record.cipher.RecordCipher;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Robert Merget <robert.merget@rub.de>
 */
public abstract class RecordLayer {

    protected static final Logger LOGGER = LogManager.getLogger("RecordLayer");

    public abstract List<AbstractRecord> parseRecords(byte[] rawBytes);

    public abstract void decryptRecord(AbstractRecord records);

    public abstract byte[] prepareRecords(byte[] data, ProtocolMessageType contentType, List<AbstractRecord> records);

    public abstract void setRecordCipher(RecordCipher cipher);

    public abstract void updateEncryptionCipher();

    public abstract void updateDecryptionCipher();

    public abstract AbstractRecord getFreshRecord();
}