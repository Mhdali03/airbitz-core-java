/**
 * Copyright (c) 2014, Airbitz Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Redistribution or use of modified source code requires the express written
 *    permission of Airbitz Inc.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the Airbitz Project.
 */

package co.airbitz.core;

import co.airbitz.internal.Jni;
import co.airbitz.internal.core;
import co.airbitz.internal.tABC_Error;
import co.airbitz.internal.tABC_CC;
import co.airbitz.internal.SWIGTYPE_p_long;
import co.airbitz.internal.SWIGTYPE_p_p_char;

public class UnsentTransaction {
    private Account mAccount;
    private Wallet mWallet;
    private String mRawTx;
    private String mTxId;
    private SpendTarget mSpendTarget;

    UnsentTransaction(Account account, Wallet wallet, String rawtx, SpendTarget spendTarget) {
        mAccount = account;
        mWallet = wallet;
        mRawTx = rawtx;
        mSpendTarget = spendTarget;
    }

    public String base16Tx() {
        return mRawTx;
    }

    public boolean broadcast() {
        tABC_Error error = new tABC_Error();
        core.ABC_SpendBroadcastTx(
                mAccount.username(), mWallet.id(),
                mSpendTarget._pSpend, mRawTx, error);
        return error.getCode() == tABC_CC.ABC_CC_Ok;
    }

    public Transaction save() {
        String id = null;
        tABC_Error error = new tABC_Error();
        SWIGTYPE_p_long txid = core.new_longp();
        SWIGTYPE_p_p_char pTxId = core.longp_to_ppChar(txid);
        core.ABC_SpendSaveTx(
                mAccount.username(), mWallet.id(),
                mSpendTarget._pSpend, mRawTx, pTxId, error);
        if (error.getCode() == tABC_CC.ABC_CC_Ok) {
            mTxId = Jni.getStringAtPtr(core.longp_value(txid));
            updateTransaction();
            return mWallet.transaction(mTxId);
        } else {
            mTxId = null;
            return null;
        }
    }

    public String txId() {
        return mTxId;
    }

    private void updateTransaction() {
        String categoryText = "Transfer:Wallet:";
        Wallet destWallet = null;
        if (mSpendTarget._pSpend != null) {
            destWallet = mAccount.wallet(mSpendTarget._pSpend.getSzDestUUID());
        }

        Transaction tx = mWallet.transaction(mTxId);
        if (null != tx) {
            if (destWallet != null) {
                tx.meta().name(destWallet.name());
                tx.meta().category(categoryText + destWallet.name());
            }
            if (mSpendTarget.getAmountFiat() > 0) {
                tx.meta().fiat(mSpendTarget.getAmountFiat());
            }
            if (0 < mSpendTarget.getBizId()) {
                tx.meta().bizid(mSpendTarget.getBizId());
            }
            try {
                tx.save();
            } catch (AirbitzException e) {
                AirbitzCore.loge("updateTransaction 1 error:");
            }
        }

        // This was a transfer
        if (destWallet != null) {
            Transaction destTx = destWallet.transaction(mTxId);
            if (null != destTx) {
                destTx.meta().name(mWallet.name());
                destTx.meta().category(categoryText + mWallet.name());
                try {
                    destTx.save();
                } catch (AirbitzException e) {
                    AirbitzCore.loge("updateTransaction 2 error:");
                }
            }
        }
    }
}
