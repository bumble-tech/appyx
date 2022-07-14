# Contributing guidelines

We love external contributions! 

Here are some guidelines to streamline the process.


## Before coding

If you have a new idea:

1. Please check open and closed issues and PRs for relevant discussions
2. If none exist, raise the topic and discuss to make sure it's aligned with the overall vision and roadmap

## Coding

1. Fork the repo
2. Create a branch in your fork (prefer - in naming rather than _)
3. Implement your changes in your branch
    - Make sure your branch contains changes limited to the scope of the task
    - Dependency updates should be standalone PRs whenever possible
    - Implement tests if applicable
6. Do a round of manual QA 
8. Update `CHANGELOG.md` if applicable
9. Update documentation if applicable
10. Push to your fork's branch, open a PR

Trivial fixes (typo, easy-to-fix compilation error, etc.) don't need to go through this process


## Documentation

When updating the documentation, test the generated mkdocs site locally.

### Setup

**Prerequisites**

- Python

```bash
pip install mkdocs
pip install mkdocs mkdocs-material
```


### Run

```bash
mkdocs serve
```

Check the console output for a localhost url, most probably something like:

```
INFO     -  [09:41:08] Serving on http://127.0.0.1:8000/bumble-tech/appyx/
```

Open the url in your browser. Changes are automatically deployed by mkdocs while the server is running.

