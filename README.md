

# This is backend test - Nguyen Xuan Loc

Foobar is a Python library for dealing with word pluralization.

## Installation

Using docker.

```bash
docker pull locnx1105/backend-test
```

## Usage
###Variable :
1. email_template: your email template path.
2. customer_csv: your customer csv path.
3. output_email : output email folder path.
4. errors_csv : your error csv path.

```python
# build 'docker image'
docker build .

# returns 'geese'
foobar.pluralize('goose')

# returns 'phenomenon'
foobar.singularize('phenomena')
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[Loc](locnx1105@gmail.com)