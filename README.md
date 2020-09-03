
<h2>GitHub Workflow Collector</h2>

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Gitter Chat](https://badges.gitter.im/Join%20Chat.svg)](https://www.apache.org/licenses/LICENSE-2.0)

A collector to showcase the GitHub Workflow details
<ul>
  <li>https://docs.github.com/en/rest/reference/actions#list-repository-workflows/</li>
  <li>https://docs.github.com/en/rest/reference/actions#list-workflow-runs</li>
  <li>https://docs.github.com/en/rest/reference/actions#list-jobs-for-a-workflow-run</li>
</ul> 

<h3>How are Workflows Organized?</h3>
<ul>
  <li>Each GitHub repository can contain multiple workflows</li>
  <li>There can be one or more "run" associated with each workflow</li>
  <li>There can be one or more "job" associated with each run</li>
  <li>There can be one or more "step" within each job</li>
</ul>

<h3>Technical Description</h3>
  The Classes and their description
<ul>
  <li>WorkflowController: Exposes APIs for UI to get the Component Data</li>
  <li>WorkflowCollectorTask : As with the framework , implementing Collector Task to regularly collect the workflow details and feed into the MongoDb</li>
  <li>WorkflowServiceImpl: To populate the componentData model and send back to UI</li>
  <li>Workflow*Repository: Provides CRUD operations to elements in the MongoDb</li>
</ul>

<h3>APIs Supporting Workflow Collector</h3>

https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/workflows?branch=main

{
"total_count": 1,
"workflows": [
{
"id": 2056361,
"node_id": "MDg6V29ya2Zsb3cyMDU2MzYx",
"name": "CI",
"path": ".github/workflows/main.yml",
"state": "active",
"created_at": "2020-07-31T07:58:51.000Z",
"updated_at": "2020-07-31T07:58:51.000Z",
"url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/workflows/2056361",
"html_url": "https://github.com/prash897/hygieia-workflow-github-collector/blob/main/.github/workflows/main.yml",
"badge_url": "https://github.com/prash897/hygieia-workflow-github-collector/workflows/CI/badge.svg"
}
]
}

https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/workflows/2056361/runs?branch=main
(only showing run_number=16)

{
"total_count": 16,
"workflow_runs": [
{
"id": 215712255,
"node_id": "MDExOldvcmtmbG93UnVuMjE1NzEyMjU1",
"head_branch": "main",
"head_sha": "78d0d92bee17e59f75d229d83a568e9b0e45fea1",
"run_number": 16,
"event": "push",
"status": "completed",
"conclusion": "success",
"workflow_id": 2056361,
"url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/runs/215712255",
"html_url": "https://github.com/prash897/hygieia-workflow-github-collector/actions/runs/215712255",
"pull_requests": [],
"created_at": "2020-08-19T19:39:50Z",
"updated_at": "2020-08-19T19:40:06Z",
"jobs_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/runs/215712255/jobs",
"logs_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/runs/215712255/logs",
"check_suite_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/check-suites/1072336053",
"artifacts_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/runs/215712255/artifacts",
"cancel_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/runs/215712255/cancel",
"rerun_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/runs/215712255/rerun",
"workflow_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/workflows/2056361",
"head_commit": {
"id": "78d0d92bee17e59f75d229d83a568e9b0e45fea1",
"tree_id": "5555d85eba4bf5969ecd5b65aa76c736e52cbcf3",
"message": "Work on README.md",
"timestamp": "2020-08-19T19:39:23Z",
"author": {
"name": "johnmcentire",
"email": "john.mcentire1@wipro.com"
},
"committer": {
"name": "johnmcentire",
"email": "john.mcentire1@wipro.com"
}
},
"repository": {
"id": 283535593,
"node_id": "MDEwOlJlcG9zaXRvcnkyODM1MzU1OTM=",
"name": "hygieia-workflow-github-collector",
"full_name": "prash897/hygieia-workflow-github-collector",
"private": false,
"owner": {
"login": "prash897",
"id": 11367273,
"node_id": "MDQ6VXNlcjExMzY3Mjcz",
"avatar_url": "https://avatars2.githubusercontent.com/u/11367273?v=4",
"gravatar_id": "",
"url": "https://api.github.com/users/prash897",
"html_url": "https://github.com/prash897",
"followers_url": "https://api.github.com/users/prash897/followers",
"following_url": "https://api.github.com/users/prash897/following{/other_user}",
"gists_url": "https://api.github.com/users/prash897/gists{/gist_id}",
"starred_url": "https://api.github.com/users/prash897/starred{/owner}{/repo}",
"subscriptions_url": "https://api.github.com/users/prash897/subscriptions",
"organizations_url": "https://api.github.com/users/prash897/orgs",
"repos_url": "https://api.github.com/users/prash897/repos",
"events_url": "https://api.github.com/users/prash897/events{/privacy}",
"received_events_url": "https://api.github.com/users/prash897/received_events",
"type": "User",
"site_admin": false
},
"html_url": "https://github.com/prash897/hygieia-workflow-github-collector",
"description": "[WIP]Hygieia collector to collect Workflow metadata associated with Github Repositories",
"fork": true,
"url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector",
"forks_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/forks",
"keys_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/keys{/key_id}",
"collaborators_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/collaborators{/collaborator}",
"teams_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/teams",
"hooks_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/hooks",
"issue_events_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/issues/events{/number}",
"events_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/events",
"assignees_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/assignees{/user}",
"branches_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/branches{/branch}",
"tags_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/tags",
"blobs_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/git/blobs{/sha}",
"git_tags_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/git/tags{/sha}",
"git_refs_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/git/refs{/sha}",
"trees_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/git/trees{/sha}",
"statuses_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/statuses/{sha}",
"languages_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/languages",
"stargazers_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/stargazers",
"contributors_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/contributors",
"subscribers_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/subscribers",
"subscription_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/subscription",
"commits_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/commits{/sha}",
"git_commits_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/git/commits{/sha}",
"comments_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/comments{/number}",
"issue_comment_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/issues/comments{/number}",
"contents_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/contents/{+path}",
"compare_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/compare/{base}...{head}",
"merges_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/merges",
"archive_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/{archive_format}{/ref}",
"downloads_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/downloads",
"issues_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/issues{/number}",
"pulls_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/pulls{/number}",
"milestones_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/milestones{/number}",
"notifications_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/notifications{?since,all,participating}",
"labels_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/labels{/name}",
"releases_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/releases{/id}",
"deployments_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/deployments"
},
"head_repository": {
"id": 283535593,
"node_id": "MDEwOlJlcG9zaXRvcnkyODM1MzU1OTM=",
"name": "hygieia-workflow-github-collector",
"full_name": "prash897/hygieia-workflow-github-collector",
"private": false,
"owner": {
"login": "prash897",
"id": 11367273,
"node_id": "MDQ6VXNlcjExMzY3Mjcz",
"avatar_url": "https://avatars2.githubusercontent.com/u/11367273?v=4",
"gravatar_id": "",
"url": "https://api.github.com/users/prash897",
"html_url": "https://github.com/prash897",
"followers_url": "https://api.github.com/users/prash897/followers",
"following_url": "https://api.github.com/users/prash897/following{/other_user}",
"gists_url": "https://api.github.com/users/prash897/gists{/gist_id}",
"starred_url": "https://api.github.com/users/prash897/starred{/owner}{/repo}",
"subscriptions_url": "https://api.github.com/users/prash897/subscriptions",
"organizations_url": "https://api.github.com/users/prash897/orgs",
"repos_url": "https://api.github.com/users/prash897/repos",
"events_url": "https://api.github.com/users/prash897/events{/privacy}",
"received_events_url": "https://api.github.com/users/prash897/received_events",
"type": "User",
"site_admin": false
},
"html_url": "https://github.com/prash897/hygieia-workflow-github-collector",
"description": "[WIP]Hygieia collector to collect Workflow metadata associated with Github Repositories",
"fork": true,
"url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector",
"forks_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/forks",
"keys_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/keys{/key_id}",
"collaborators_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/collaborators{/collaborator}",
"teams_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/teams",
"hooks_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/hooks",
"issue_events_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/issues/events{/number}",
"events_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/events",
"assignees_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/assignees{/user}",
"branches_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/branches{/branch}",
"tags_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/tags",
"blobs_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/git/blobs{/sha}",
"git_tags_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/git/tags{/sha}",
"git_refs_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/git/refs{/sha}",
"trees_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/git/trees{/sha}",
"statuses_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/statuses/{sha}",
"languages_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/languages",
"stargazers_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/stargazers",
"contributors_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/contributors",
"subscribers_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/subscribers",
"subscription_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/subscription",
"commits_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/commits{/sha}",
"git_commits_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/git/commits{/sha}",
"comments_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/comments{/number}",
"issue_comment_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/issues/comments{/number}",
"contents_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/contents/{+path}",
"compare_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/compare/{base}...{head}",
"merges_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/merges",
"archive_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/{archive_format}{/ref}",
"downloads_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/downloads",
"issues_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/issues{/number}",
"pulls_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/pulls{/number}",
"milestones_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/milestones{/number}",
"notifications_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/notifications{?since,all,participating}",
"labels_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/labels{/name}",
"releases_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/releases{/id}",
"deployments_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/deployments"
}
}}]

https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/runs/215712255/jobs?branch=main?branch=main

{
"total_count": 1,
"jobs": [
{
"id": 1004646359,
"run_id": 215712255,
"run_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/runs/215712255",
"node_id": "MDg6Q2hlY2tSdW4xMDA0NjQ2MzU5",
"head_sha": "78d0d92bee17e59f75d229d83a568e9b0e45fea1",
"url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/actions/jobs/1004646359",
"html_url": "https://github.com/prash897/hygieia-workflow-github-collector/runs/1004646359",
"status": "completed",
"conclusion": "success",
"started_at": "2020-08-19T19:39:58Z",
"completed_at": "2020-08-19T19:40:01Z",
"name": "build",
"steps": [
{
"name": "Set up job",
"status": "completed",
"conclusion": "success",
"number": 1,
"started_at": "2020-08-19T19:39:58.000Z",
"completed_at": "2020-08-19T19:39:59.000Z"
},
{
"name": "Run actions/checkout@v2",
"status": "completed",
"conclusion": "success",
"number": 2,
"started_at": "2020-08-19T19:39:59.000Z",
"completed_at": "2020-08-19T19:40:01.000Z"
},
{
"name": "Run a one-line script",
"status": "completed",
"conclusion": "success",
"number": 3,
"started_at": "2020-08-19T19:40:01.000Z",
"completed_at": "2020-08-19T19:40:01.000Z"
},
{
"name": "Run a multi-line script",
"status": "completed",
"conclusion": "success",
"number": 4,
"started_at": "2020-08-19T19:40:01.000Z",
"completed_at": "2020-08-19T19:40:01.000Z"
},
{
"name": "Post Run actions/checkout@v2",
"status": "completed",
"conclusion": "success",
"number": 8,
"started_at": "2020-08-19T19:40:01.000Z",
"completed_at": "2020-08-19T19:40:01.000Z"
},
{
"name": "Complete job",
"status": "completed",
"conclusion": "success",
"number": 9,
"started_at": "2020-08-19T19:40:01.000Z",
"completed_at": "2020-08-19T19:40:01.000Z"
}
],
"check_run_url": "https://api.github.com/repos/prash897/hygieia-workflow-github-collector/check-runs/1004646359"
}
]
}