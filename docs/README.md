# Bruh — User Guide

> A lightweight task chatbox you run from the terminal (and GUI). Type natural-ish commands, get things done.

---

## Quick start

**Requirements:** Java 11+ and Gradle.

**Run:**

```bash
./gradlew run
```

You’ll see a chat-style interface. Type commands (see below). Data is auto-saved to `data/bruh.txt`.

**Conventions used in this guide**

* `<angle brackets>` mean *your input* (don’t type the brackets).
* Indexes shown by `list` are **1‑based**.

---

## Features at a glance

* Add **todos**, **deadlines**, and **events**
* **List** tasks
* **Mark/Unmark** done
* **Delete** tasks
* **Find** tasks by keywords (OR logic)
* **Reschedule** deadlines (shift by days or set an absolute date)
* **Autosave** to `data/bruh.txt`

---

## Commands

| Feature                     | Command format                                | Example                                                     | Notes                                                                              |                                                                                    |
| --------------------------- | --------------------------------------------- | ----------------------------------------------------------- | ---------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------- |
| List tasks                  | `list`                                        | `list`                                                      | Shows all tasks with their **1‑based** indexes.                                    |                                                                                    |
| Add todo                    | `todo <description>`                          | `todo buy milk`                                             |                                                                                    |                                                                                    |
| Add deadline                | `deadline <description> /by <date>`           | `deadline CS2103 iP /by 2025-09-30`                         | Use date formats in the section below.                                             |                                                                                    |
| Add event                   | `event <description> /from <start> /to <end>` | `event meeting /from 2025-09-30 14:00 /to 2025-09-30 15:30` | Start must be before end.                                                          |                                                                                    |
| Mark done                   | `mark <index>`                                | `mark 2`                                                    | Index from `list`.                                                                 |                                                                                    |
| Unmark                      | `unmark <index>`                              | `unmark 2`                                                  |                                                                                    |                                                                                    |
| Delete                      | `delete <index>`                              | `delete 3`                                                  |                                                                                    |                                                                                    |
| Find                        | `find <keyword ...>`                          | `find book` · `find data structures`                        | Matches **any** keyword in the **description** (OR). Currently **case‑sensitive**. |                                                                                    |
| Reschedule (deadlines only) | \`resched <index> <+Nd                        | YYYY-MM-DD>\`                                               | `resched 2 +3d` · `resched 2 2025-10-01`                                           | `+Nd` shifts forward by N days (e.g., `+2d`). Use a date to move earlier or later. |
| Exit                        | `bye`                                         | `bye`                                                       | Saves and quits.                                                                   |                                                                                    |

### Date & time formats

Bruh understands ISO‑style inputs best:

* **Date:** `YYYY-MM-DD` (e.g., `2025-09-30`)
* **Date + time:** `YYYY-MM-DD HH:MM` or `YYYY-MM-DDTHH:MM` (for events)

If a time is omitted where allowed, midnight (00:00) is assumed. If you get a format error, rewrite in one of the forms above.

---

## Examples

```
list

todo read book

deadline return book /by 2025-09-20

resched 2 +3d

event CS2103 meeting /from 2025-09-30 14:00 /to 2025-09-30 15:30

find book

mark 1

delete 1

bye
```

---

## Storage

* File: `data/bruh.txt`
* On first launch, if the file doesn’t exist, Bruh starts with an empty list and creates the file on save.
* If saving fails (e.g., permissions), Bruh still works for the current session but warns you.

---

## Tips & notes

* Indexes are **1-based**.
* `find` looks only in the **description**, not in dates.
* `resched` works on **Deadline** tasks. To modify an **Event**, delete and re‑add it for now.
* The GUI and CLI share the same logic, so commands are the same.

---

## Troubleshooting

* **“Unrecognized date format”** → Use `YYYY-MM-DD` (and `HH:MM` for events).
* **“No such task”** → Run `list` to confirm the correct **1‑based** index.
* **Changes don’t appear** → Run `list` again; for deadlines, use `resched` to update the stored date.

---

## Credits

If you reused code or ideas, acknowledge them here (e.g., parsing helpers, date utilities). Example:

* Date parsing approach inspired by Java Time API examples from the OpenJDK docs.

---

## About

**Product name:** Bruh
