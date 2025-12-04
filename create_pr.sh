#!/bin/bash
# Usage: ./create_pr.sh <GITHUB_TOKEN>
if [ -z "$1" ]; then
  echo "Usage: ./create_pr.sh <GITHUB_TOKEN>" && exit 1
fi
TOKEN=$1
BODY=$(awk '{gsub(/"/, "\\\""); print}' PR_DESCRIPTION.md)
curl -X POST -H "Authorization: token ${TOKEN}" -H "Accept: application/vnd.github+json" \
  https://api.github.com/repos/olenabughunter/mobileapp_ai_show_case/pulls \
  -d "{\"title\":\"Add full feature & edge-case UI tests (fc_01–fc_05, ec_01–ec_06)\", \"head\":\"fix/ct_02_toast_flakiness\", \"base\":\"main\", \"body\":\"${BODY}\"}"
