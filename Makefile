.PHONY: help start-be start-fe start stop-be stop-fe stop restart-be restart-fe restart attach-be attach-fe status

SHELL := /bin/bash
BE_SESSION  := opportune-be
FE_SESSION  := opportune-fe
PID_DIR     := .pids
LOG_DIR     := .logs

# --- Detect available process manager ---
RUNNER := $(shell \
  if command -v tmux  >/dev/null 2>&1; then echo tmux; \
  elif command -v screen >/dev/null 2>&1; then echo screen; \
  else echo fallback; fi)

$(shell mkdir -p $(PID_DIR) $(LOG_DIR))

# ==================== HELP ====================
help:
	@echo ""
	@echo "  Running mode: $(RUNNER)"
	@echo ""
	@echo "  make start        → start both BE and FE"
	@echo "  make start-be     → start Spring Boot"
	@echo "  make start-fe     → start Vue dev server"
	@echo "  make stop         → stop both"
	@echo "  make stop-be      → stop Spring Boot"
	@echo "  make stop-fe      → stop Vue dev server"
	@echo "  make restart-be   → restart Spring Boot"
	@echo "  make restart-fe   → restart Vue dev server"
	@echo "  make attach-be    → view Spring Boot logs"
	@echo "  make attach-fe    → view Vue dev logs"
	@echo "  make status       → show running services"
	@echo ""

# ==================== START ====================
start-be:
ifeq ($(RUNNER),tmux)
	@tmux has-session -t $(BE_SESSION) 2>/dev/null && echo "⚠️  Spring Boot already running" && exit 0 || true
	@tmux new-session -d -s $(BE_SESSION) './gradlew bootRun'
	@echo "🚀 Spring Boot started [tmux]  →  attach: make attach-be"
else ifeq ($(RUNNER),screen)
	@screen -ls | grep -q $(BE_SESSION) && echo "⚠️  Spring Boot already running" && exit 0 || true
	@screen -dmS $(BE_SESSION) ./gradlew bootRun
	@echo "🚀 Spring Boot started [screen]  →  attach: make attach-be"
else
	@[ -f $(PID_DIR)/gradle.pid ] && kill -0 $$(cat $(PID_DIR)/gradle.pid) 2>/dev/null \
		&& echo "⚠️  Spring Boot already running" && exit 0 || true
	@nohup ./gradlew bootRun > $(LOG_DIR)/gradle.log 2>&1 & echo $$! > $(PID_DIR)/gradle.pid
	@echo "🚀 Spring Boot started [fallback]  →  logs: make attach-be"
endif

start-fe:
ifeq ($(RUNNER),tmux)
	@tmux has-session -t $(FE_SESSION) 2>/dev/null && echo "⚠️  Vue dev already running" && exit 0 || true
	@tmux new-session -d -s $(FE_SESSION) 'npm run dev'
	@echo "🚀 Vue dev started [tmux]  →  attach: make attach-fe"
else ifeq ($(RUNNER),screen)
	@screen -ls | grep -q $(FE_SESSION) && echo "⚠️  Vue dev already running" && exit 0 || true
	@screen -dmS $(FE_SESSION) npm run dev
	@echo "🚀 Vue dev started [screen]  →  attach: make attach-fe"
else
	@[ -f $(PID_DIR)/npm.pid ] && kill -0 $$(cat $(PID_DIR)/npm.pid) 2>/dev/null \
		&& echo "⚠️  Vue dev already running" && exit 0 || true
	@nohup npm run dev > $(LOG_DIR)/npm.log 2>&1 & echo $$! > $(PID_DIR)/npm.pid
	@echo "🚀 Vue dev started [fallback]  →  logs: make attach-fe"
endif

start: start-be start-fe

# ==================== STOP ====================
stop-be:
ifeq ($(RUNNER),tmux)
	@tmux kill-session -t $(BE_SESSION) 2>/dev/null && echo "✅ Spring Boot stopped" || echo "ℹ️  Not running"
else ifeq ($(RUNNER),screen)
	@screen -S $(BE_SESSION) -X quit 2>/dev/null && echo "✅ Spring Boot stopped" || echo "ℹ️  Not running"
else
	@[ -f $(PID_DIR)/gradle.pid ] \
		&& kill $$(cat $(PID_DIR)/gradle.pid) 2>/dev/null \
		&& rm -f $(PID_DIR)/gradle.pid \
		&& echo "✅ Spring Boot stopped" \
		|| echo "ℹ️  Not running"
endif

stop-fe:
ifeq ($(RUNNER),tmux)
	@tmux kill-session -t $(FE_SESSION) 2>/dev/null && echo "✅ Vue dev stopped" || echo "ℹ️  Not running"
else ifeq ($(RUNNER),screen)
	@screen -S $(FE_SESSION) -X quit 2>/dev/null && echo "✅ Vue dev stopped" || echo "ℹ️  Not running"
else
	@[ -f $(PID_DIR)/npm.pid ] \
		&& kill $$(cat $(PID_DIR)/npm.pid) 2>/dev/null \
		&& rm -f $(PID_DIR)/npm.pid \
		&& echo "✅ Vue dev stopped" \
		|| echo "ℹ️  Not running"
endif

stop: stop-be stop-fe

# ==================== RESTART ====================
restart-be: stop-be start-be
restart-fe: stop-fe start-fe
restart: stop start

# ==================== ATTACH / LOGS ====================
attach-be:
ifeq ($(RUNNER),tmux)
	@tmux attach-session -t $(BE_SESSION)
else ifeq ($(RUNNER),screen)
	@screen -r $(BE_SESSION)
else
	@[ -f $(LOG_DIR)/gradle.log ] && tail -f $(LOG_DIR)/gradle.log || echo "❌ No log file found. Is Spring Boot running?"
endif

attach-fe:
ifeq ($(RUNNER),tmux)
	@tmux attach-session -t $(FE_SESSION)
else ifeq ($(RUNNER),screen)
	@screen -r $(FE_SESSION)
else
	@[ -f $(LOG_DIR)/npm.log ] && tail -f $(LOG_DIR)/npm.log || echo "❌ No log file found. Is Vue dev running?"
endif

# ==================== STATUS ====================
status:
	@echo "  Mode: $(RUNNER)"
	@echo ""
ifeq ($(RUNNER),tmux)
	@tmux has-session -t $(BE_SESSION) 2>/dev/null \
		&& echo "  🟢 Spring Boot: RUNNING  (make attach-be)" \
		|| echo "  🔴 Spring Boot: STOPPED"
	@tmux has-session -t $(FE_SESSION) 2>/dev/null \
		&& echo "  🟢 Vue dev:     RUNNING  (make attach-fe)" \
		|| echo "  🔴 Vue dev:     STOPPED"
else ifeq ($(RUNNER),screen)
	@screen -ls | grep -q $(BE_SESSION) \
		&& echo "  🟢 Spring Boot: RUNNING  (make attach-be)" \
		|| echo "  🔴 Spring Boot: STOPPED"
	@screen -ls | grep -q $(FE_SESSION) \
		&& echo "  🟢 Vue dev:     RUNNING  (make attach-fe)" \
		|| echo "  🔴 Vue dev:     STOPPED"
else
	@[ -f $(PID_DIR)/gradle.pid ] && kill -0 $$(cat $(PID_DIR)/gradle.pid) 2>/dev/null \
		&& echo "  🟢 Spring Boot: RUNNING  (make attach-be)" \
		|| echo "  🔴 Spring Boot: STOPPED"
	@[ -f $(PID_DIR)/npm.pid ] && kill -0 $$(cat $(PID_DIR)/npm.pid) 2>/dev/null \
		&& echo "  🟢 Vue dev:     RUNNING  (make attach-fe)" \
		|| echo "  🔴 Vue dev:     STOPPED"
endif
