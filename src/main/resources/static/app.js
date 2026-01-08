let cfg;

function status(msg, err) {
    const s = document.getElementById('status');
    s.textContent = msg;
    s.className = 'status ' + (err ? 'error' : 'success');
}

async function load() {
    const r = await fetch('/admin/config');
    cfg = await r.json();

    serverPort.value = cfg.server.port;

    dbUser.value = cfg.spring.datasource.username;
    dbPass.value = cfg.spring.datasource.password;
    dbUrl.value = cfg.spring.datasource.url;
    dbDriver.value = cfg.spring.datasource.driverClassName;

    ddlAuto.value = cfg.spring.jpa.hibernate.ddlAuto;
    showSql.checked = cfg.spring.jpa.showSql;

    webEnabled.checked = cfg.support.web.enabled;
    adminUser.value = cfg.support.web.admin.username;
    adminPass.value = cfg.support.web.admin.password;

    msgMaxLength.value = cfg.support.messages.maxLength;
    msgLimitTime.value = cfg.support.messages.limitTime;
    msgLimitAmount.value = cfg.support.messages.limitAmountmsg;
    msgRestrictTime.value = cfg.support.messages.limitRestrictTime;
    msgGreeting.checked = cfg.support.messages.greetingEnabled;
    msgHeader.checked = cfg.support.messages.headerEnabled;
    msgFooter.checked = cfg.support.messages.footerEnabled;

    tgEnabled.checked = cfg.support.platforms.telegram.enabled;
    tgToken.value = cfg.support.platforms.telegram.token;
    tgChat.value = cfg.support.platforms.telegram.supportChatId;

    renderTexts(cfg.support.texts);
}

function renderTexts(texts) {
    const c = document.getElementById('texts');
    c.innerHTML = '';
    Object.keys(texts).sort().forEach(k => {
        c.innerHTML += `
            <div class="row">
                <label>${k}</label>
                <textarea data-key="${k}">${texts[k]}</textarea>
            </div>`;
    });
}

async function save() {
    cfg.server.port = Number(serverPort.value);

    cfg.spring.datasource.username = dbUser.value;
    cfg.spring.datasource.password = dbPass.value;
    cfg.spring.datasource.url = dbUrl.value;
    cfg.spring.datasource.driverClassName = dbDriver.value;

    cfg.spring.jpa.hibernate.ddlAuto = ddlAuto.value;
    cfg.spring.jpa.showSql = showSql.checked;

    cfg.support.web.enabled = webEnabled.checked;
    cfg.support.web.admin.username = adminUser.value;
    cfg.support.web.admin.password = adminPass.value;

    cfg.support.messages.maxLength = Number(msgMaxLength.value);
    cfg.support.messages.limitTime = Number(msgLimitTime.value);
    cfg.support.messages.limitAmountmsg = Number(msgLimitAmount.value);
    cfg.support.messages.limitRestrictTime = Number(msgRestrictTime.value);
    cfg.support.messages.greetingEnabled = msgGreeting.checked;
    cfg.support.messages.headerEnabled = msgHeader.checked;
    cfg.support.messages.footerEnabled = msgFooter.checked;

    cfg.support.platforms.telegram.enabled = tgEnabled.checked;
    cfg.support.platforms.telegram.token = tgToken.value;
    cfg.support.platforms.telegram.supportChatId = Number(tgChat.value);

    document.querySelectorAll('#texts textarea').forEach(t => {
        cfg.support.texts[t.dataset.key] = t.value;
    });

    await fetch('/admin/config', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(cfg)
    });

    status('Saved. Restart application to apply changes.', false);
}

window.onload = load;
