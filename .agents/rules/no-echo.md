---
name: No Echo Commands
description: Disables the use of echo commands for polling or waiting.
trigger: always_on
---

# No Echo Commands

**Do NOT use `echo` commands to wait or poll for background tasks!**
Antigravity is fully reactive. When you launch a background task, a subagent, or schedule a timer, the framework will automatically notify you and wake you up when it's done.

Instead of running an `echo` command to waste time, simply **stop making tool calls** and finish your response. The framework will naturally give you the next turn when an event occurs.
